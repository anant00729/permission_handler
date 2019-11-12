package com.binarynumbers.gokozo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash_name.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_name)



//        web_view.loadUrl("file:///android_asset/component.html")
//
//        val webSetting = web_view.getSettings()
//        webSetting.setJavaScriptEnabled(true)
//        webSetting.setDisplayZoomControls(true)


        //


        Glide.with(this)
            .load("file:///android_asset/animation2.gif")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {




                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }

                    return false
                }
            })
        .into(web_view)

//        Glide.with(this)
//            .load("file:///android_asset/animation2.gif")
//            .listener()
//            .into( GifDrawableImageViewTarget(web_view, 1))
//
//
//
//            {
//
//
//                (var resource Drawable, var model Object , var target Target<Drawable>, val dataSource DataSource, val isFirstResource boolean)->
//
//
//            }
//
//

        
        


        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 12500)

    }

    /**
     * A target for display [GifDrawable] or [Drawable] objects in [ImageView]s.
     */

}



