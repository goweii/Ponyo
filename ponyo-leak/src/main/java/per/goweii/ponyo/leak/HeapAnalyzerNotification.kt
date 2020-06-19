package per.goweii.ponyo.leak

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

internal class HeapAnalyzerNotification(context: Context) {

    private val channelId: String = "ponyo_leak_chanel_id_heap_analyzer"
    private val channelName: String = "Heap analyzer"
    private val channelDesc: String = "Heap analyzer"
    private val notificationManagerCompat: NotificationManagerCompat =
        NotificationManagerCompat.from(context)
    private val notificationCompatBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            channel.description = channelDesc
            channel.enableVibration(false)
            channel.enableLights(false)
            channel.setSound(null, null)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    fun get(step: String): Notification {
        return notificationCompatBuilder
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setContentTitle("Heap analyzer")
            .setContentText(step)
            .build()
    }
}