package per.goweii.ponyo.leak

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import shark.OnAnalysisProgressListener
import java.util.*

internal class HeapAnalyzerNotification(
    private val service: Service
) {
    private val channelId: String = "ponyo_leak_chanel_id_heap_analyzer"
    private val channelName: String = "Ponyo Heap Analyzer"
    private val channelDesc: String = "Generate heap analysis report"
    private val notificationManagerCompat: NotificationManagerCompat =
        NotificationManagerCompat.from(service)
    private val notificationCompatBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(service, channelId)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            channel.description = channelDesc
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    fun startForeground(percent: Float, desc: String) {
        val indeterminate = percent < 0F
        val max = 10000
        val progress = when  {
            percent < 0 -> 0
            percent > 1 -> 1
            else -> (max * percent).toInt()
        }
        val notification = notificationCompatBuilder
            .setSmallIcon(R.drawable.ponyo_leak_ic_notification)
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Heap analyzer")
            .setContentText(desc)
            .setProgress(max, progress, indeterminate)
            .build()
        service.startForeground(100, notification)
    }

    fun stopForeground() {
        service.stopForeground(true)
    }
}