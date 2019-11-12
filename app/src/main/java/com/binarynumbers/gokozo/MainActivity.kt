package com.binarynumbers.gokozo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), OnContentShare, OnExitCalled{
    override fun onShareLink(link: String?) {


        val shareBody = "Here is the share content body"
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link)
        startActivity(sharingIntent)


    }



    override fun onExit() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)




        var setting = wb_view.settings
        setting.javaScriptEnabled = true


        var webinterface = WebInterface(this, this)

        wb_view.addJavascriptInterface(WebInterface(this, this), "Android")

        setting.loadWithOverviewMode = true
        setting.useWideViewPort = true
        setting.domStorageEnabled = true


        WebView.setWebContentsDebuggingEnabled(true)


        wb_view.webViewClient = MyWebViewClient(this)




        _showDynamicLink()
        FirebaseMessaging.getInstance().subscribeToTopic("TOPIC_NAME");


        this.webView = wb_view


//        wb_view.loadUrl("javascript:window.Android.firebase = 'Hi!';");
//
//
//        wb_view.evaluateJavascript("javascript:window.Android.firebase = 'Hi!';");
//
//        wb_view.loadUrl("javascript:window.firebase = 'Hi!';");
//        wb_view.evaluateJavascript("javascript:window.firebase = 'Hi!';");
//        wb_view.evaluateJavascript("javascript:localStorage.setItem('test','firebasetest')");
//
//        wb_view.loadUrl("javascript:localStorage.setItem('test','firebasetest')");
//        wb_view.loadUrl("javascript:window.Android.showToast('asjdhjkashdkasjdhkashdasjkdhkajshdkahdjashdkasdh')");



        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                val token = instanceIdResult.token
                webinterface.token = token
                val preference=getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
                val editor=preference.edit()
                editor.putString("Token",token)
                editor.apply()
            })


        //wb_view.loadData("javascript:localStorage.setItem('test','firebasetest')");




    }





    var webView: WebView? = null

    var preUrl : String = ""


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {


        if(is_exit_called){
            onBackPressed()
        }else {

            this.webView?.let {
                if (keyCode == KEYCODE_BACK && it.canGoBack()) {
                    it.url?.let { _url ->

                        if("https://gokozo.binarynumbers.io/daily-feeds" == _url){
                            is_exit_called = true
                        }else{
                            is_exit_called = false
                            it.goBack()
                        }
                        preUrl = _url
                    }
                    return true
                }
            }

        }



        return super.onKeyDown(keyCode, event)

    }


    override fun onShare(shareContent: String?) {
        shareContent?.let {

            val b = ScreenShot.takescreenshotOfRootView(wb_view,wb_view.width, wb_view.height);

            val uri = saveImage(b)
            shareImageUri(uri!!, shareContent)

//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.type="text/plain"
//            shareIntent.putExtra(Intent.EXTRA_TEXT, it)
//            startActivity(Intent.createChooser(shareIntent,"asdhjkashd"))
        }
    }


    private fun saveImage(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()

            uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

        } catch (e: IOException) {
            print( "IOException while trying to write file for sharing: " + e.message)
        }

        return uri
    }


    private fun shareImageUri(uri: Uri, shareContent: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
//        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }


    var tap_count = 0

    override fun onBackPressed() {


        if(tap_count == 0){
            ++tap_count
            Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                tap_count = 0
            }, 2000)
        }else {
            super.onBackPressed()
        }


    }


    var is_exit_called : Boolean = false

    override fun onExit(b: Boolean) {
        this.is_exit_called = b
    }



    private fun _showDynamicLink() {




        //wb_view.loadUrl("https://app-dev.gokozo.com")

        FirebaseApp.initializeApp(this)
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    print(deepLink)



//                    is_from_dynamic_link = true
//
//
                    val data = deepLink!!.path!!

                    print(data)

                    wb_view.loadUrl("https://app-dev.gokozo.com" + deepLink.path)

                    //pendingDynamicLinkData.getLink().
                }else {

                    wb_view.loadUrl("https://app-dev.gokozo.com")
//                    mMVM.getMovieDatails(this, mov_id)
//                    mMVM.getSpShowTimes(this, null, mov_id, schID, false)
                }
            }
            .addOnFailureListener(this) { e -> }
    }


}

private fun WebView.evaluateJavascript(s: String) {

}


