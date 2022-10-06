package com.daily.dayo.data.datasource.remote.firebase

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
import com.daily.dayo.presentation.activity.MainActivity
import com.daily.dayo.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.data.isNotEmpty()) {
            val body = remoteMessage.data["body"]
            val postId = remoteMessage.data["postId"]
            val memberId = remoteMessage.data["memberId"]
            sendNotification(body = body, postId = postId, memberId = memberId)
        }else if (remoteMessage.notification != null) {
            val body = remoteMessage.notification!!.body
            sendNotification(body = body, postId = null, memberId = null)
        }
    }

    private fun sendNotification(body: String?, postId: String?, memberId: String?){
        val id = System.currentTimeMillis().toInt()

        // notification 클릭 시 이동하는 액티비티
        val notiIntent = Intent(this, MainActivity::class.java)
        notiIntent.putExtra("ExtraFragment", "Notification")
        notiIntent.putExtra("PostId", postId)
        notiIntent.putExtra("MemberId", memberId)

        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, id, notiIntent, PendingIntent.FLAG_IMMUTABLE)

        // notification channel 생성
        val CHANNEL_ID = getString(R.string.notification_channel_id)
        val CHANNEL_NAME = getString(R.string.notification_channel_name)
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

    suspend fun getCurrentToken() = suspendCoroutine<String> { continuation ->
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
        DayoApplication.preferences.fcmDeviceToken = getCurrentToken()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    fun unregisterFcmToken() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
    }
}