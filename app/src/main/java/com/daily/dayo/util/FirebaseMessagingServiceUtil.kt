package com.daily.dayo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.daily.dayo.DayoApplication
import com.daily.dayo.MainActivity
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseMessagingServiceUtil: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.data.isNotEmpty()) {
            val body = remoteMessage.data["body"]
            sendNotification(body)
        }else if (remoteMessage.notification != null) {
            val body = remoteMessage.notification!!.body
            sendNotification(body)
        }
    }

    private fun sendNotification(body: String?){
        val id = System.currentTimeMillis().toInt()

        // notification 클릭 시 이동하는 액티비티
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // notification channel 생성
        val CHANNEL_ID = "Channel"
        val CHANNEL_NAME = "서비스 알림"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // notification 생성
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dayo_logo)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        notificationManager.notify(id, notificationBuilder.build())
    }

    private suspend fun getCurrentToken() = suspendCoroutine<String> { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful){
                val token = task.result
                Log.d(TAG, token)
                continuation.resume(token)
            } else {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                continuation.resume("")
                return@OnCompleteListener
            }
        })
    }

    suspend fun registerFcmToken() {
        SharedManager(DayoApplication.applicationContext()).fcmDeviceToken = getCurrentToken()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    fun unregisterFcmToken() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        FirebaseMessaging.getInstance().deleteToken()
    }
}