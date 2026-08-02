package com.stgeorge.church.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.stgeorge.church.models.User;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * إدارة حسابات الخدام — manager can only ADD and DELETE (by explicit
 * request); there is no admin-side edit or enable/disable action in the UI.
 *
 * SECURITY MODEL (no secrets ever ship in the app):
 *
 * — Creating a servant runs on a second, temporary FirebaseApp instance so
 *   the manager's own session is never touched. This uses the same public
 *   client config already in google-services.json — nothing extra to protect.
 *
 * — Deleting a servant does two independent things:
 *     1. ALWAYS deletes the Firestore profile directly from the app. This
 *        alone is enough on its own: with no profile, AuthHelper#login
 *        refuses the account regardless of whether the underlying Firebase
 *        Auth record still technically exists (see AuthHelper#fetchUserProfile).
 *        This step needs no server and no elevated privileges — a manager
 *        is already allowed to write to /users under normal Firestore rules.
 *     2. BEST-EFFORT calls an optional Cloud Function ("deleteServantAuth")
 *        that fully deletes the Firebase Auth record too. Actually deleting
 *        another user's Auth account requires the Admin SDK, which can only
 *        run on a trusted server — never inside the app. The function itself
 *        re-checks (server-side, not trusting the client) that the caller is
 *        a manager and that the target is a servant before deleting anything.
 *        If that function isn't deployed, this call simply fails silently —
 *        step 1 already made the account fully unusable, so nothing about
 *        app security depends on step 2 existing. See /functions in the
 *        project root for the function's source and deploy instructions.
 *
 * No API key, service-account credential, or admin secret of any kind is
 * embedded in this class or anywhere else in the app.
 */
public class AdminUserRepository {

    public interface ListCallback {
        void onSuccess(List<User> servants);
        void onFailure(String message);
    }

    public interface WriteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private static final String SECONDARY_APP_NAME = "AdminAccountCreation";

    private final Context appContext;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AdminUserRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    private String usernameToEmail(String username) {
        return username.trim().toLowerCase() + "@stgeorge.church";
    }

    public void getAllServants(ListCallback callback) {
        firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("role", Constants.ROLE_SERVANT)
                .orderBy("fullName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void createServant(String username, String password, String fullName, String phone,
                               WriteCallback callback) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.length() < 6
                || fullName == null || fullName.trim().isEmpty()) {
            callback.onFailure("INVALID_INPUT");
            return;
        }

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance(SECONDARY_APP_NAME);
        } catch (IllegalStateException notInitializedYet) {
            secondaryApp = FirebaseApp.initializeApp(
                    appContext, FirebaseApp.getInstance().getOptions(), SECONDARY_APP_NAME);
        }
        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        String email = usernameToEmail(username);
        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    User servant = new User(uid, username.trim(), fullName.trim(), Constants.ROLE_SERVANT);
                    servant.setPhone(phone);

                    firestore.collection(Constants.COLLECTION_USERS)
                            .document(uid)
                            .set(servant)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure("PROFILE_SAVE_ERROR"));

                    // Clean up the temporary session — the manager's real
                    // session lives on the default FirebaseApp and is untouched.
                    secondaryAuth.signOut();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // This username's underlying Auth record still exists —
                        // most likely from a previously deleted servant whose
                        // Auth account wasn't fully removed (see class javadoc).
                        callback.onFailure("USERNAME_TAKEN");
                    } else {
                        callback.onFailure("CREATE_ERROR");
                    }
                });
    }

    /**
     * حذف الخادم. Always removes the Firestore profile (this alone fully
     * blocks the account app-wide); then best-effort asks the optional Cloud
     * Function to also delete the underlying Firebase Auth record. The
     * callback reflects step 1 only, since that's the step this app can
     * actually guarantee without a server.
     */
    public void deleteServant(String userId, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnSuccessListener(unused -> {
                    callback.onSuccess();
                    tryDeleteAuthAccountBestEffort(userId);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void tryDeleteAuthAccountBestEffort(String userId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("targetUserId", userId);
            FirebaseFunctions.getInstance()
                    .getHttpsCallable("deleteServantAuth")
                    .call(data);
            // Intentionally not attaching a listener: whether this succeeds,
            // fails, or the function doesn't exist at all, the servant is
            // already fully locked out via the Firestore deletion above.
        } catch (Exception ignored) {
            // Cloud Function not deployed / no network — nothing to do here;
            // this step was always a best-effort cleanup, not a dependency.
        }
    }

    private List<User> toList(QuerySnapshot snapshot) {
        List<User> list = new ArrayList<>();
        if (snapshot != null) {
            snapshot.forEach(doc -> {
                User user = doc.toObject(User.class);
                if (user != null) {
                    user.setUserId(doc.getId());
                    list.add(user);
                }
            });
        }
        return list;
    }
}
