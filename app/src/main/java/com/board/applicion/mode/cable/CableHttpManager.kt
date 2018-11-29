package com.board.applicion.mode.cable

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.Nullable
import com.board.applicion.app.App
import com.board.applicion.mode.SPConstant.SP_BASE_URL
import com.board.applicion.mode.SPConstant.SP_NAME
import com.library.utils.SPHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class CableHttpManager<T>(val lifecycle: Lifecycle) : LifecycleObserver {

    private var okHttpClient: OkHttpClient? = null
    var retrofit: Retrofit? = null

    init {
        try {
            lifecycle.addObserver(this)
            okHttpClient = OkHttpClient().newBuilder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build()
            var baseUrl = SPHelper.readString(App.instance, SP_NAME, SP_BASE_URL, "http://118.24.162.247:8080/")
            if (baseUrl.startsWith("http://") && baseUrl.endsWith("/")) {

            } else {
                baseUrl = "http://118.24.162.247:8080/"
            }
            if (okHttpClient != null)
                retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(okHttpClient!!)
                        .addConverterFactory(ProtoConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        } catch (e: Exception) {

        }

    }

    @Nullable
    fun requestData(call: Call<CableBaseEntity<T>>?, successCallBack: (T?) -> Unit, failCallBack: (String?) -> Unit) {
        try {
            if (call == null) {
                failCallBack("请求失败！")
                return
            }
            call.enqueue(object : Callback<CableBaseEntity<T>> {

                override fun onFailure(call: Call<CableBaseEntity<T>>?, t: Throwable?) {
                    failCallBack(t?.message)
                }

                override fun onResponse(call: Call<CableBaseEntity<T>>?, response: Response<CableBaseEntity<T>>?) {
                    if (response != null && response.isSuccessful) {
                        val result = response.body()
                        if (result?.errorCode == 0) {
                            requestSuccess(successCallBack, result.data!!)
                        } else {
                            requestFail(failCallBack, "请求错误")
                        }
                    } else {
                        requestFail(failCallBack, response?.message())
                    }
                }

            })
        } catch (e: IOException) {
            e.printStackTrace()
            requestFail(failCallBack, e.message)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun requestSuccess(successCallBack: (T?) -> Unit, t: T) {

        successCallBack(t)

    }

    private fun requestFail(failCallBack: (String?) -> Unit, message: String?) {

        failCallBack(message)

    }

}