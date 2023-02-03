package com.xint.chatlibrary.network

import android.annotation.SuppressLint
import com.xint.chatlibrary.core.ChatCore
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.utils.LibraryLogs
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


object ApiClient {

    private var retrofit: Retrofit? = null
    private var requiredToken: Boolean = false

    fun getRetrofit(requiredToken: Boolean = true): Retrofit? {


        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS)
        if (requiredToken) {
            okHttpClientBuilder.addInterceptor {
                val headersBuilder = Headers.Builder()
                headersBuilder.add("Content-Type", "application/json")
                ChatCore.instance?.token?.let { sessionId ->
                    headersBuilder.add("sessionId", sessionId)
                }
                val originalRequest = it.request()
                val request = originalRequest.newBuilder()
                    .headers(headersBuilder.build())
                    .method(originalRequest.method, originalRequest.body)
                    .build()
                LibraryLogs.debug("Api Request-->", "$request")
                it.proceed(request)
            }
            if (!this.requiredToken) {
                this.requiredToken = true
                retrofit = null
            }
        } else {
            if (this.requiredToken) {
                this.requiredToken = false
                retrofit = null
            }
        }
        val okHttpClient: OkHttpClient = okHttpClientBuilder.build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(ChatCore.instance!!.baseUrl)
                .client(okHttpClient)
                .build()
        }
        return retrofit
    }


    fun retrofitCall(
        call: Call<ResponseBody?>, listener: ResponseListener
    ) {
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                listener.onLoading(isLoading = false)
                if (response.isSuccessful/*response.code() == 200 || response.code() == 201*/) {
                    try {
                        response.body()?.let {
                            val responseJson = JSONObject(it.string())
                            if (responseJson.has("statusCode")) {
                                if (
                                    responseJson.getInt("statusCode") > 300
                                ) {
                                    listener.onErrorBody(responseJson)
                                } else {
                                    listener.onSuccess(responseJson)
                                }
                            }
                        }

                    } catch (e: Exception) {
                        e.message?.let {
                            listener.onFailure(it)
                        }
                    }
                }
                response.errorBody()?.let {
                    try {
                        val errorResponseJson = JSONObject(it.string())
                        listener.onErrorBody(errorResponseJson)
                    } catch (e: JSONException) {
                        listener.onFailure(e.toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                listener.onLoading(isLoading = false)
                t.message?.let {
                    listener.onFailure(it)
                }

            }
        })
    }

}