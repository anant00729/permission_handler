package com.binarynumbers.gokozo.netwoking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var ourInstance : Retrofit? = null

    val instance : Retrofit
        get() {
            if(ourInstance == null){


                val _c = OkHttpClient().newBuilder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY


                    })
                    .build()


//                http://192.168.1.27:3000/
//                https://mns-app.herokuapp.com/

                ourInstance = Retrofit.Builder()
                    .baseUrl("http://api.gokozo.com/v1/")
                    .client(_c)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return ourInstance!!
        }
}