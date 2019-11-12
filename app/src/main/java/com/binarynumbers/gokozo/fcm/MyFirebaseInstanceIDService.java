package com.binarynumbers.gokozo.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.messaging.FirebaseMessagingService;





public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();




    @Override
    public void onNewToken(@NonNull String s) {

        Log.e(TAG, "onNewToken: " + s );
        super.onNewToken(s);
    }


    //    @Inject
//    ServiceAPI mServiceAPI;

//    @Override
//    public void onTokenRefresh() {
//        //AAAp.getAppComponent(this).inject(this);
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//
//        saveTheDataToSP(refreshedToken);
//
//        String manufacturer = Build.MANUFACTURER;
//        String model = Build.MODEL;
//        //FCMModel fcm_m = new FCMModel(refreshedToken, manufacturer, model);
//
////        mServiceAPI.sendFCMToken(fcm_m)
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(this::handleSubmit,this::hanleErr);
//
//    }

    private void saveTheDataToSP(String refreshedToken) {
//        SharedPreferences sp = getSharedPreferences(SP_USER_FILE, Context.MODE_PRIVATE);
//        SharedPreferences.Editor mE = sp.edit();
//        mE.putString(APP_FCM_TOKEN, refreshedToken);
//        mE.apply();
    }

//    private void handleSubmit(FCMRes mFCMRes) {
//        Log.e(TAG, "handleSubmit: " + mFCMRes);
//    }



    private void hanleErr(Throwable throwable) {
        Log.e(TAG, "hanleErr: " + throwable.getMessage() );
    }
}
