package code_grow.dohahub.app.utils


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.MainActivity
import com.google.firebase.installations.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class DohaFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message)
    }

    private fun sendNotification(message: RemoteMessage) {
        val data = message.data
        val title = data["title"]
        val body = data["body"]

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setDefaults(Notification.DEFAULT_SOUND)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))
            .setContentTitle(title!!)
            .setContentText(body!!)
            .setVibrate(longArrayOf(0, 500, 100))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)





        notificationManager.notify(Random.nextInt(1000), notificationBuilder.build())
    }
}