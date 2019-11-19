package com.binarynumbers.gokozo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

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
    public String getDeviceType() {
        return "Android";
    }





}


