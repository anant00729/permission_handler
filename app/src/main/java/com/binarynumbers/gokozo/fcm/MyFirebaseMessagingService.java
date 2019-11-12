package com.binarynumbers.gokozo.fcm;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Build;
import android.util.Log;


import com.binarynumbers.gokozo.MainActivity;
import com.binarynumbers.gokozo.fcm.Config;
import com.binarynumbers.gokozo.fcm.NotificationUtils;
import com.binarynumbers.gokozo.fcm.model.NotiSubModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;


    @Override
    public void
    onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "onMessageReceived: " + Build.VERSION.SDK_INT);
        Log.e(TAG, "onMessageReceived: " + Build.VERSION.RELEASE);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
////            handleNotification(remoteMessage.getNotification().getBody());
//        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + String.valueOf(remoteMessage.getData()));

            try {
                JSONObject json = new JSONObject(String.valueOf(remoteMessage.getData().get("data")));
                Gson gson = new Gson();
                NotiSubModel model = gson.fromJson(json.toString(), NotiSubModel.class);
                handleDataMessage(model);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
//        if(message.length()>10){
//            sendNotification(message);
//        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleDataMessage(NotiSubModel data) {

        String title = data.getTitle();
        String message = data.getMessage();
        try {

            Intent resultIntent = null;
                //SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                // jab app ON hai
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.getMessage());


                    if(data.getImage() == null){
                        showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
                    }else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, "", resultIntent, data.getImage());
                    }


                    // app is in foreground, broadcast the push message
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("message", message);
//                    //pushNotification.putExtra("image", imageUrl);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    // play notification sound
//
//                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                    notificationUtils.playNotificationSound();
                }
                // jab app OFF hai
                else {

                    Log.e(TAG, " isAppIsInBackground "  );
                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", message);

                    if(data.getImage() == null){
                        showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
                    }else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, "", resultIntent, data.getImage());
                    }



                    //Log.e(TAG, "handleDataMessage: " + imageUrl );

                    //showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    // check for image attachment
//                    if (TextUtils.isEmpty(imageUrl)) {
//                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                    } else {
//
//                        // image is present, show notification with image
//                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                    }

                }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }




    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {

        Log.e(TAG, "showNotificationMessageWithBigImage: " + title );
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }





}
