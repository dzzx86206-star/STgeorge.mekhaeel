/**
 * deleteServantAuth — OPTIONAL. Fully deletes a servant's Firebase Auth
 * record (the Android app already deletes their Firestore profile on its
 * own, which alone is enough to fully lock them out — see
 * AdminUserRepository.java's javadoc). This function just removes the
 * leftover Auth record too, so the same username can be reused later.
 *
 * Deploying this is NOT required for the app to be secure or functional.
 * It's purely a cleanup convenience.
 *
 * SECURITY: this never trusts the client. It re-checks, using the caller's
 * own signed Firebase ID token (context.auth — cannot be forged from a
 * client, unlike anything sent in `data`), that the caller's Firestore
 * profile has role == 'manager', and that the target account's role is
 * 'servant' (when the target profile still exists), before deleting
 * anything. No credentials of any kind live in the Android app — this
 * function runs only on Google's servers under your Firebase project.
 */

const { onCall, HttpsError } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

admin.initializeApp();

exports.deleteServantAuth = onCall(async (request) => {
  const auth = request.auth;
  if (!auth) {
    throw new HttpsError("unauthenticated", "يجب تسجيل الدخول");
  }

  const targetUserId = request.data && request.data.targetUserId;
  if (!targetUserId || typeof targetUserId !== "string") {
    throw new HttpsError("invalid-argument", "targetUserId مطلوب");
  }
  if (targetUserId === auth.uid) {
    throw new HttpsError("invalid-argument", "لا يمكن حذف حسابك الخاص بهذه الطريقة");
  }

  const db = admin.firestore();

  const callerDoc = await db.collection("users").doc(auth.uid).get();
  if (!callerDoc.exists || callerDoc.data().role !== "manager") {
    throw new HttpsError("permission-denied", "هذا الإجراء للمدير فقط");
  }

  // If the profile still exists, refuse to touch anything but a servant.
  // (Normally the app has already deleted this doc before calling here —
  // this check only matters if the function is ever called independently.)
  const targetDoc = await db.collection("users").doc(targetUserId).get();
  if (targetDoc.exists && targetDoc.data().role !== "servant") {
    throw new HttpsError("permission-denied", "هذه الدالة تحذف حسابات الخدام فقط");
  }

  await db.collection("users").doc(targetUserId).delete().catch(() => {});
  await admin.auth().deleteUser(targetUserId);

  return { success: true };
});
