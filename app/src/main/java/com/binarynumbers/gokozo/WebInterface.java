package com.binarynumbers.gokozo;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebInterface {

    Context mContext;
    OnContentShare mL;

    /** Instantiate the interface and set the context */
    WebInterface(Context c,OnContentShare mL ) {
        mContext = c;
        this.mL = mL;
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




}


