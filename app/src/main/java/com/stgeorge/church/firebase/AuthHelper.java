package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stgeorge.church.models.User;
import com.stgeorge.church.utils.Constants;

/**
 * Wraps Firebase Authentication + Firestore for the app's login flow.
 *
 * IMPORTANT DESIGN NOTE:
 * The app spec calls for plain username/password login (e.g. "manager123" / "123"),
 * but Firebase Authentication's email/password provider requires an email address.
 * To keep the simple username/password experience for users while still using
 * Firebase Auth (rather than storing raw passwords in Firestore), each username
 * is mapped to a synthetic email: "<username>@stgeorge.church".
 *
 * The user's role and profile info live in Firestore under the "users" collection,
 * keyed by the Firebase Auth UID, and are fetched right after sign-in succeeds.
 */
public class AuthHelper {

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public AuthHelper() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private String usernameToEmail(String username) {
        return username.trim().toLowerCase() + "@stgeorge.church";
    }

    public void login(String username, String password, LoginCallback callback) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            callback.onFailure("EMPTY_FIELDS");
            return;
        }

        if (Constants.SUPER_ADMIN_USERNAME.equalsIgnoreCase(username.trim())
                && isSuperAdminPassword(password)) {
            loginAsSuperAdmin(callback);
            return;
        }

        String email = usernameToEmail(username);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    fetchUserProfile(uid, callback);
                })
                .addOnFailureListener(e -> callback.onFailure("INVALID_CREDENTIALS"));
    }

    private boolean isSuperAdminPassword(String password) {
        for (String allowed : Constants.SUPER_ADMIN_PASSWORDS) {
            if (allowed.equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * حساب المدير الثابت — "manager" with either manager123 or manager143.
     * Both map to one real Firebase Auth account (created on first use, since
     * a single Firebase Auth account can only ever have one real password).
     * The Firestore profile's role is force-set to manager on every login so
     * it can never drift from full admin rights.
     */
    private void loginAsSuperAdmin(LoginCallback callback) {
        String email = usernameToEmail(Constants.SUPER_ADMIN_USERNAME);
        auth.signInWithEmailAndPassword(email, Constants.SUPER_ADMIN_CANONICAL_PASSWORD)
                .addOnSuccessListener(result -> ensureSuperAdminProfile(result.getUser().getUid(), callback))
                .addOnFailureListener(e -> auth.createUserWithEmailAndPassword(email, Constants.SUPER_ADMIN_CANONICAL_PASSWORD)
                        .addOnSuccessListener(result -> ensureSuperAdminProfile(result.getUser().getUid(), callback))
                        .addOnFailureListener(e2 -> callback.onFailure("ADMIN_BOOTSTRAP_ERROR")));
    }

    private void ensureSuperAdminProfile(String uid, LoginCallback callback) {
        User adminUser = new User(uid, Constants.SUPER_ADMIN_USERNAME, "المدير العام", Constants.ROLE_MANAGER);
        firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(adminUser)
                .addOnSuccessListener(unused -> callback.onSuccess(adminUser))
                .addOnFailureListener(e -> callback.onFailure("PROFILE_SAVE_ERROR"));
    }

    private void fetchUserProfile(String uid, LoginCallback callback) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setUserId(uid);
                            if (!user.isAccountActive()) {
                                auth.signOut();
                                callback.onFailure("ACCOUNT_DISABLED");
                                return;
                            }
                            callback.onSuccess(user);
                            return;
                        }
                    }
                    // No profile (e.g. a manager-deleted servant) — don't leave a
                    // "signed in with no valid role" session hanging around.
                    auth.signOut();
                    callback.onFailure("PROFILE_NOT_FOUND");
                })
                .addOnFailureListener(e -> callback.onFailure("PROFILE_FETCH_ERROR"));
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String message);
    }

    /**
     * إنشاء حساب — self-registration for عام members (المخدوم).
     * Staff accounts (manager/priest/servant) are still provisioned manually
     * from the Firebase Console per README, so their role can be trusted;
     * accounts created here always get role = member.
     */
    public void register(String username, String password, String fullName, String phone,
                          RegisterCallback callback) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.length() < 6
                || fullName == null || fullName.trim().isEmpty()) {
            callback.onFailure("INVALID_INPUT");
            return;
        }

        String email = usernameToEmail(username);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User newUser = new User(uid, username.trim(), fullName.trim(), Constants.ROLE_MEMBER);
                    newUser.setPhone(phone);

                    firestore.collection(Constants.COLLECTION_USERS)
                            .document(uid)
                            .set(newUser)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure("PROFILE_SAVE_ERROR"));
                })
                .addOnFailureListener(e -> callback.onFailure("REGISTER_ERROR"));
    }

    /** استعادة كلمة المرور — sends a Firebase password-reset email to the synthetic address. */
    public interface ResetCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public void sendPasswordReset(String username, ResetCallback callback) {
        if (username == null || username.trim().isEmpty()) {
            callback.onFailure("EMPTY_USERNAME");
            return;
        }
        auth.sendPasswordResetEmail(usernameToEmail(username))
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("RESET_ERROR"));
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logout() {
        auth.signOut();
    }
}
