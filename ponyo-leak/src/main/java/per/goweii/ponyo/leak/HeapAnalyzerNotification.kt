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

    fun startForeground(step: OnAnalysisProgressListener.Step?) {
        val notification = notificationCompatBuilder
            .setSmallIcon(R.drawable.ponyo_leak_ic_notification)
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Heap analyzer")
            .setContentText(step.desc)
            .setProgress(maxProgress, step.progress, step == null)
            .build()
        service.startForeground(100, notification)
    }

    fun stopForeground() {
        service.stopForeground(true)
    }

    private val maxProgress: Int = OnAnalysisProgressListener.Step.values().size

    private val OnAnalysisProgressListener.Step?.progress: Int
        get() = this?.ordinal ?: 0

    private val OnAnalysisProgressListener.Step?.desc: String
        get() = when (this) {
            OnAnalysisProgressListener.Step.PARSING_HEAP_DUMP -> "Parsing heap dump"
            OnAnalysisProgressListener.Step.EXTRACTING_METADATA -> "Extracting metadata"
            OnAnalysisProgressListener.Step.FINDING_RETAINED_OBJECTS -> "Finding retained objects"
            OnAnalysisProgressListener.Step.FINDING_PATHS_TO_RETAINED_OBJECTS -> "Finding paths to retained objects"
            OnAnalysisProgressListener.Step.FINDING_DOMINATORS -> "Finding dominators"
            OnAnalysisProgressListener.Step.COMPUTING_NATIVE_RETAINED_SIZE -> "Computing native retained size"
            OnAnalysisProgressListener.Step.COMPUTING_RETAINED_SIZE -> "Computing retained size"
            OnAnalysisProgressListener.Step.BUILDING_LEAK_TRACES -> "Building leak traces"
            OnAnalysisProgressListener.Step.REPORTING_HEAP_ANALYSIS -> "Reporting heap analysis"
            else -> "Waiting analyze"
        }
}