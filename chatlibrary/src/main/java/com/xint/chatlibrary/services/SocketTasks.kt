package com.xint.chatlibrary.services
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xint.chatlibrary.core.ChatCore
import com.xint.chatlibrary.utils.LibraryLogs
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object SocketTasks {
    private const val TAG = "SocketTask-->"
    private var retry = true
    private lateinit var mSocket: Socket
    private var options = IO.Options()

    init {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
        options.callFactory = clientBuilder.build()
        mSocket = IO.socket(ChatCore.instance?.socketUrl, options)
    }

    fun initializeSocket(context: Context) {

        mSocket.connect()

        mSocket.on(Socket.EVENT_CONNECT) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_CONNECT}", it.toString())
            }
            LibraryLogs.debug(TAG, Socket.EVENT_CONNECT)
//            val bundle = Bundle()
//            val intent = Intent(Constants.SOCKET_TASK)
//            intent.putExtras(bundle)
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        mSocket.on(Socket.EVENT_DISCONNECT) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_DISCONNECT}", it.toString())
            }
            if (retry){
                mSocket.connect()
            }
        }

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_CONNECT_ERROR}", it.toString())
            }

            if (retry){
                mSocket.connect()
            }

        }

    }

    fun disconnect() {
        retry = false
        mSocket.disconnect()
    }

    fun isSocketConnected(): Boolean {
        return mSocket.connected()
    }


}