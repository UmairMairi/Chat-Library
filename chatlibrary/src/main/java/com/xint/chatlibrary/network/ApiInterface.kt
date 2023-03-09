package com.xint.chatlibrary.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("chat/start-conversation")
    fun startConversation(@Body requestParam: RequestBody?): Call<ResponseBody?>?

    @GET("chat/get-conversations")
    fun getConversation(@Query("page") page: Int, @Query("limit") limit: Int): Call<ResponseBody?>?

    @GET("chat/get-archived-conversations")
    fun getArchivedConversations(@Query("page") page: Int, @Query("limit") limit: Int): Call<ResponseBody?>?

    @GET("chat/get-blocked-users")
    fun getBlockedUsers(): Call<ResponseBody?>?

    @GET("chat/get-user-messages/{user_id}")
    fun getUserMessages(@Path("user_id") userId: String?): Call<ResponseBody?>?

    @Multipart
    @POST("chat/upload-media-file")
    fun uploadMediaFile(@Part mediaFile: Part?): Call<ResponseBody?>?

    @GET("chat/get-user-details/{receiverId}")
    fun getUserDetails(@Path("receiverId") receiverId: String?): Call<ResponseBody?>?


    @GET("chat/get-last-seen/{receiverId}")
    fun getLastSeen(@Path("receiverId") receiverId: String?): Call<ResponseBody?>?

    @GET("chat/update-chat-user-last-seen/{receiverId}")
    fun updateLastSeen(@Path("receiverId") receiverId: String?): Call<ResponseBody?>?

    @PATCH("chat/delete-all-message/{conversationId}")
    fun deleteAllMessage(@Path("conversationId") conversationId: String?, @Body requestBody: RequestBody?
    ): Call<ResponseBody?>?

    @PATCH("/chat/archive-unarchive-chat/{conversationId}")
    fun archiveUnarchiveChat(@Path("conversationId") conversationId: String?, @Body requestBody: RequestBody?
    ): Call<ResponseBody?>?

    @POST("chat/mark-messages")
    fun markAllRead(@Body requestBody: RequestBody?): Call<ResponseBody?>?

    @GET("chat/get-unread-messages-count")
    fun getUnreadMessageCount(): Call<ResponseBody?>?

    @POST("chat/get-presigned-url")
    fun getPreSignedUrl(@Body requestBody: RequestBody?): Call<ResponseBody?>?


    @POST("chat/upload-presigned")
    fun uploadPreSignedFile(@Body requestBody: RequestBody?): Call<ResponseBody?>?

    @PUT
    fun uploadBinaryMedia(@Url url: String?, @Body requestBody: RequestBody?): Call<ResponseBody?>?

    @GET
    fun dynamicUrl(@Url url: String?): Call<ResponseBody?>?


    @POST("/sendotp")
    fun sendOtp(@Body data: JsonObject?): Call<ResponseBody?>?

    @POST("/verifyotp")
    fun verifyOtp(@Body data: JsonObject?): Call<ResponseBody?>?


}