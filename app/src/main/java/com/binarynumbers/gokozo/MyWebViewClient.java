package com.binarynumbers.gokozo;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.*;

class MyWebViewClient extends WebViewClient {

    private static final String TAG = "MyWebViewClient";


    OnExitCalled exitCallback;

    MyWebViewClient(OnExitCalled exitCallback){
        this.exitCallback = exitCallback;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {


        view.loadUrl(req.toString());


        Log.e(TAG, "shouldOverrideUrlLoading: ");

        return true;
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {


        Log.e(TAG, "shouldOverrideKeyEvent: " );
        return super.shouldOverrideKeyEvent(view, event);
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        Log.e(TAG, "shouldInterceptRequest: " );


        String exit_url = "https://peoplemattersapi.binarynumbers.io/bigNews";
        if(request.getUrl().equals(exit_url)){
            if (this.exitCallback != null) {
                this.exitCallback.onExit(true);
            }
        }else {
            this.exitCallback.onExit(false);
        }



        return super.shouldInterceptRequest(view, request);
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Log.e(TAG, "shouldOverrideUrlLoading: ");

        return super.shouldOverrideUrlLoading(view, url);
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        Log.e(TAG, "shouldInterceptRequest: ");

        return super.shouldInterceptRequest(view, url);
    }


    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {

        Log.e(TAG, "onSafeBrowsingHit: ");

        super.onSafeBrowsingHit(view, request, threatType, callback);
    }


    @Override
    public void onPageFinished(WebView view, String url) {

        Log.e(TAG, "onPageFinished: ");

        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        Log.e(TAG, "onPageStarted: " + url);

        super.onPageStarted(view, url, favicon);




    }



}