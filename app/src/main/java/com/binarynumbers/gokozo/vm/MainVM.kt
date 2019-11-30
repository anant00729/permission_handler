package com.binarynumbers.gokozo.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binarynumbers.gokozo.models.ImageRes
import com.binarynumbers.gokozo.netwoking.RetrofitClient
import com.binarynumbers.gokozo.netwoking.ServiceAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainVM : ViewModel() {

    val l_res = MutableLiveData<ImageRes?>()

    var _s: ServiceAPI
    internal var _cD : CompositeDisposable

    init {
        var _r = RetrofitClient.instance
        _s = _r.create(ServiceAPI::class.java)
        _cD = CompositeDisposable()

    }


    fun initImageUpload() {
//        _cD.add(_s.uploadImage()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe (
//                {res ->
//                    when (res.code()) {
//                        200 -> {
//                            print(res.body())
//                        }
//                        else -> {
//
//                        }
//                    }
//                }, {e ->
//                    print(e)
//                })
////            .subscribe ({ p -> displayData(p)}, {e ->
////                print(e)
////            })
//
//        )
    }



}