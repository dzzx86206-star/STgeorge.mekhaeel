package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Receives push notifications sent by the manager (news, announcements,
 * events, or direct messages) via Firebase Cloud Messaging.
 *
 * TODO (next stage): build the actual notification UI (icon, sound, deep link
 * into the relevant section) and register the FCM token to Firestore per user.
 */
public class STgeorgeFcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO: build and show a notification using remoteMessage.getNotification()
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // TODO: save this token to the user's Firestore document so the manager
        // can target notifications correctly.
    }
}
