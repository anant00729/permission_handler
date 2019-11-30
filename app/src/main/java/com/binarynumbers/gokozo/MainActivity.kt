package com.binarynumbers.gokozo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.WindowManager
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.binarynumbers.gokozo.models.ImageRes
import com.binarynumbers.gokozo.netwoking.RetrofitClient
import com.binarynumbers.gokozo.netwoking.ServiceAPI
import com.binarynumbers.gokozo.vm.MainVM
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Response
import retrofit2.http.Part
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), OnContentShare, OnExitCalled{



    private val asw_file_req = 1
    private var asw_file_path: ValueCallback<Array<Uri>>? = null
    private var asw_cam_message: String? = null
    private var asw_file_message: ValueCallback<Uri>? = null

    internal var ASWP_FUPLOAD = true
    internal var ASWV_F_TYPE = "*/*"
    internal var ASWP_MULFILE = true
    internal var ASWP_CAMUPLOAD = true
    internal var ASWP_ONLYCAM = true
    internal var ASWP_PBAR = false

    // show progress bar in app
    private val TAG = MainActivity::class.java.getSimpleName()

    private val file_perm = 2

    override fun onShareLink(link: String?) {
        val shareBody = "Here is the share content body"
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link)
        startActivity(sharingIntent)
    }



    override fun onExit() {
    }


    //lateinit var mLVM: MainVM
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)
        //mLVM = ViewModelProviders.of(this).get(MainVM::class.java)
        _observe()


        var setting = wb_view.settings
        setting.javaScriptEnabled = true
        var webinterface = WebInterface(this, this)
        wb_view.addJavascriptInterface(WebInterface(this, this), "Android")

        setting.loadWithOverviewMode = true
        setting.useWideViewPort = true
        setting.domStorageEnabled = true
        setting.allowFileAccess = true
        setting.allowFileAccessFromFileURLs = true
        setting.allowUniversalAccessFromFileURLs = true
        setting.useWideViewPort = true
        setting.domStorageEnabled = true

        WebView.setWebContentsDebuggingEnabled(true)


        wb_view.webViewClient = MyWebViewClient(this)
        _showDynamicLink()
        FirebaseMessaging.getInstance().subscribeToTopic("TOPIC_NAME");


        this.webView = wb_view

        webView?.setOnLongClickListener { true }
        webView?.isHapticFeedbackEnabled = false






        webView?.webChromeClient = object : WebChromeClient() {
            //Handling input[type="file"] requests for android API 16+
            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
                if (ASWP_FUPLOAD) {
                    asw_file_message = uploadMsg
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = ASWV_F_TYPE
                    if (ASWP_MULFILE) {
                        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    startActivityForResult(Intent.createChooser(i, getString(R.string.fl_chooser)), asw_file_req)
                }
            }

            //Handling input[type="file"] requests for android API 21+
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                if (check_permission(2) && check_permission(3)) {
                    if (ASWP_FUPLOAD) {
                        if (asw_file_path != null) {
                            asw_file_path!!.onReceiveValue(null)
                        }
                        asw_file_path = filePathCallback
                        var takePictureIntent: Intent? = null
                        if (ASWP_CAMUPLOAD) {
                            takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            if (takePictureIntent.resolveActivity(this@MainActivity.packageManager) != null) {
                                var photoFile: File? = null
                                try {
                                    photoFile = create_image()
                                    takePictureIntent.putExtra("PhotoPath", asw_cam_message)
                                } catch (ex: IOException) {
                                    Log.e(TAG, "Image file creation failed", ex)
                                }

                                if (photoFile != null) {
                                    asw_cam_message = "file:" + photoFile.absolutePath
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                                } else {
                                    takePictureIntent = null
                                }
                            }
                        }
                        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                        if (!ASWP_ONLYCAM) {
                            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                            contentSelectionIntent.type = ASWV_F_TYPE
                            if (ASWP_MULFILE) {
                                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            }
                        }
                        val intentArray: Array<Intent?>
                        if (takePictureIntent != null) {
                            intentArray = arrayOf(takePictureIntent)
                        } else {
                            intentArray = arrayOfNulls<Intent>(0)
                        }

                        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                        chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.fl_chooser))
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                        startActivityForResult(chooserIntent, asw_file_req)
                    }
                    return true
                } else {
                    get_file()
                    return false
                }
            }

            //Getting webview rendering progress
            override fun onProgressChanged(view: WebView, p: Int) {
//                if (ASWP_PBAR) {
//                    asw_progress.progress = p
//                    if (p == 100) {
//                        asw_progress.progress = 0
//                    }
//                }
            }

            // overload the geoLocations permissions prompt to always allow instantly as app permission was granted previously
//            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
//                if (Build.VERSION.SDK_INT < 23 || check_permission(1)) {
//                    // location permissions were granted previously so auto-approve
//                    callback.invoke(origin, true, false)
//                } else {
//                    // location permissions not granted so request them
//                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), loc_perm)
//                }
//            }
        }






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


    internal lateinit var mSAPI : ServiceAPI
    internal lateinit var mCD : CompositeDisposable

    private fun _observe() {

        val mR = RetrofitClient.instance
        mSAPI = mR.create(ServiceAPI::class.java)
        mCD = CompositeDisposable()

    }


    //Creating image file for upload
    @Throws(IOException::class)
    private fun create_image(): File {
        @SuppressLint("SimpleDateFormat")
        val file_name = SimpleDateFormat("yyyy_mm_ss").format(Date())
        val new_name = "file_" + file_name + "_"
        val sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(new_name, ".jpg", sd_directory)
    }


    //Checking permission for storage and camera for writing and uploading images
    fun get_file() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

        //Checking for storage permission to write images for upload
        if (ASWP_FUPLOAD && ASWP_CAMUPLOAD && !check_permission(2) && !check_permission(3)) {
            ActivityCompat.requestPermissions(this@MainActivity, perms, file_perm)

            //Checking for WRITE_EXTERNAL_STORAGE permission
        } else if (ASWP_FUPLOAD && !check_permission(2)) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), file_perm)

            //Checking for CAMERA permissions
        } else if (ASWP_CAMUPLOAD && !check_permission(3)) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), file_perm)
        }
    }

    //Checking if particular permission is given or not
    fun check_permission(permission: Int): Boolean {
        when (permission) {
            //1 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

            2 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            3 -> return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }
        return false
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

                        if("https://app-dev.gokozo.com/index1.html" == _url){
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


            Toast.makeText(this@MainActivity, "", Toast.LENGTH_LONG).show()

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




        //wb_view.loadUrl("https://app-dev.gokozo.com/index1.html")

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

                    wb_view.loadUrl("https://app-dev.gokozo.com/index1.html" + deepLink.path)

                    //pendingDynamicLinkData.getLink().
                }else {

                    wb_view.loadUrl("https://app-dev.gokozo.com/index1.html")
//                    mMVM.getMovieDatails(this, mov_id)
//                    mMVM.getSpShowTimes(this, null, mov_id, schID, false)
                }
            }
            .addOnFailureListener(this) { e -> }
    }


    override fun openCamera() {



    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity@this)
//        when(requestCode){
//
//
//            456 -> {
//                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    pickImageFromGallery()
//                }else {
//                    Toast.makeText(this, "Permission Denied" , Toast.LENGTH_LONG).show()
//                }
//            }
//        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 123)
    }


    override fun openGallery() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, 456)
            }else {
                pickImageFromGallery()
            }
        }else {
            pickImageFromGallery()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == 123){


            //val filePath = intent?.data?.let { getRealPathFromURIPath(it, MainActivity@this) }


            try{

                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, intent?.data)

                imageToString()

                val path = ImageFilePath.getPath(MainActivity@this, intent?.data)

                var file = File(path)

                val propertyImage = RequestBody.create(MediaType.parse("image/*"), file)
                val p = MultipartBody.Part.createFormData("file", file.name, propertyImage)
                mCD.add(mSAPI.uploadImage(p)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({res -> onImageUploadRes(res)}, {e -> onGetError(e)}))


            }catch (e : IOException){
                print(e.toString())
            }




            //val surveyImagesParts = arrayOf(MultipartBody.Part);




            //val file = File()

            //val propertyImageFile = File()

//    var propertyImagePart = MultipartBody.Part.createFormData("PropertyImage", propertyImageFile.getName(), propertyImage);
//
//    val surveyImagesParts = MultipartBody.Part[surveyModel.getPicturesList().size()];
//
//    for (int index = 0; index < surveyModel.getPicturesList().size(); index++) {
//        Log.d(TAG, "requestUploadSurvey: survey image " + index + "  " + surveyModel.getPicturesList().get(index).getImagePath());
//        File file = new File(surveyModel.getPicturesList().get(index).getImagePath());
//        RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
//        surveyImagesParts[index] = MultipartBody.Part.createFormData("SurveyImage", file.getName(), surveyBody);
//    }
//
//    final WebServicesAPI webServicesAPI = RetrofitManager.getInstance().getRetrofit().create(WebServicesAPI.class);
//    Call<UploadSurveyResponseModel> surveyResponse = null;
//    if (surveyImagesParts != null) {
//        surveyResponse = webServicesAPI.uploadSurvey(surveyImagesParts, propertyImagePart, draBody);
//    }
//    surveyResponse.enqueue(this);
        }



//        if (Build.VERSION.SDK_INT >= 21) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            if (Build.VERSION.SDK_INT >= 23) {
//                window.statusBarColor =
//                    this.getResources().getColor(R.color.colorPrimary, this.getTheme())
//            } else {
//                window.statusBarColor = this.getResources().getColor(R.color.colorPrimary)
//            }
//            var results: Array<Uri>? = null
//            if (resultCode == Activity.RESULT_OK) {
//                if (requestCode == asw_file_req) {
//                    if (null == asw_file_path) {
//                        return
//                    }
//
//
//
//                    if (intent == null || intent.data == null) {
//                        if (asw_cam_message != null) {
//                            results = arrayOf(Uri.parse(asw_cam_message))
//                        }
//                    } else {
//
//
//
//
//                        val dataString = intent.dataString
//                        if (dataString != null) {
//                            results = arrayOf(Uri.parse(dataString))
//                        } else {
//                            if (ASWP_MULFILE) {
////                                if (intent.clipData != null) {
////                                    val numSelectedFiles = intent.clipData!!.itemCount
////                                    for (i in 0 until numSelectedFiles) {
////                                        results[i] = intent.clipData!!.getItemAt(i).uri
////                                    }
////                                }
//                            }
//                        }
//                    }
//
//
//                }
//            }
//
//
//
//
//
//            /*Thread({
//
//            }).start()
//*/
//
//        }

    }


    private fun imageToString() : String{
        val barray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , barray)
        val imgByte = barray.toByteArray()
        return MediaStore.Images.Media.insertImage(contentResolver, bitmap,"Title",null)

    }

     private fun getRealPathFromURIPath(contentURI : Uri , activity : Activity) : String {
        var cursor = activity?.contentResolver?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            return contentURI.path.toString()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)
        }
    }

    private fun onGetError(e: Throwable?) {
        print(e)
    }

    private fun onImageUploadRes(res: Response<ImageRes>?) {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView?.evaluateJavascript("javascript:setImage(\"" + res?.body()?.data?.image_path + "\");") {
                v ->
                print(v)
            }
        } else {
            webView?.loadUrl("javascript:setImage(\"" + res?.body()?.data?.image_path + "\");")
        }




    }


}





