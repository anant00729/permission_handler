package com.binarynumbers.gokozo.netwoking

import com.binarynumbers.gokozo.models.ImageRes
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part



interface ServiceAPI {



    @Multipart
    @POST("uploads ")
    fun uploadImage(@Part image: MultipartBody.Part? /*, @Body RegParams mReg*/): Observable<Response<ImageRes>>

}
