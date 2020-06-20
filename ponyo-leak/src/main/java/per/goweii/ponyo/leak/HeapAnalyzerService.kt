package per.goweii.ponyo.leak

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Debug
import per.goweii.ponyo.log.Ponlog
import shark.*
import java.io.File

class HeapAnalyzerService : IntentService("HeapAnalyzerService"), OnAnalysisProgressListener {

    companion object {
        private const val PARAM_HPROF_FILE = "hprofFile"

        fun start(context: Context, hprofFile: File) {
            val intent = Intent(context, HeapAnalyzerService::class.java)
            intent.putExtra(PARAM_HPROF_FILE, hprofFile)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private lateinit var heapAnalyzerNotification: HeapAnalyzerNotification

    override fun onCreate() {
        super.onCreate()
        heapAnalyzerNotification = HeapAnalyzerNotification(this)
        heapAnalyzerNotification.startForeground(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        heapAnalyzerNotification.stopForeground()
    }

    override fun onHandleIntent(intent: Intent?) {
        val hprofFile = intent?.getSerializableExtra(PARAM_HPROF_FILE) ?: return
        hprofFile as File
        Debug.dumpHprofData(hprofFile.absolutePath)
        val heapAnalyzer = HeapAnalyzer(this)
        val heapAnalysis = heapAnalyzer.analyze(
            heapDumpFile = hprofFile,
            leakingObjectFinder = WatchedRefFinder,
            referenceMatchers = AndroidReferenceMatchers.appDefaults,
            computeRetainedHeapSize = true,
            objectInspectors = AndroidObjectInspectors.appDefaults,
            metadataExtractor = AndroidMetadataExtractor,
            proguardMapping = null
        )
        Ponlog.w { "$heapAnalysis" }
        when (heapAnalysis) {
            is HeapAnalysisSuccess -> {
                val retainedObjectCount = heapAnalysis.allLeaks.sumBy { it.leakTraces.size }
                val leakTypeCount = heapAnalysis.applicationLeaks.size + heapAnalysis.libraryLeaks.size
            }
            is HeapAnalysisFailure -> {
            }
        }
    }

    override fun onAnalysisProgress(step: OnAnalysisProgressListener.Step) {
        heapAnalyzerNotification.startForeground(step)
    }
}