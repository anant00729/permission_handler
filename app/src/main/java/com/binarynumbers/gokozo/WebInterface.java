package com.binarynumbers.gokozo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WebInterface {

    Context mContext;
    OnContentShare mL;
    String token;

    /** Instantiate the interface and set the context */
    WebInterface(Context c,OnContentShare mL ) {
        mContext = c;
        this.mL = mL;
        this.token = "";
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {

        mL.onShare(toast);
    }


    @JavascriptInterface
    public void isHomePage(Boolean p) {
        mL.onExit();
    }



    @JavascriptInterface
    public void sharelink(String data) {
        mL.onShareLink(data);
    }


    @JavascriptInterface
    public String getFCM() {
        SharedPreferences preference= mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String _t = preference.getString("Token","");
        return _t;
    }


    @JavascriptInterface
    public String getPhoneModel() {
        return Build.MODEL;
    }


    @JavascriptInterface
    public String openGallery() {
        mL.openGallery();
        Log.e(TAG, "openGallery: ");
        return "";
    }


    @JavascriptInterface
    public String openCamera() {
        mL.openCamera();
        Log.e(TAG, "openCamera: " );
        return Build.MODEL;
    }


    @JavascriptInterface
    public String getDeviceType() {
        return "Android";
    }


//    @JavascriptInterface
//    public String setImage(String image) {
//        return "Android";
//    }







}


