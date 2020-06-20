package per.goweii.ponyo.leak

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import per.goweii.ponyo.leak.HeapAnalysisFormatter.toFormatString
import per.goweii.ponyo.log.Ponlog
import shark.*
import java.io.File

class HeapAnalyzerService : IntentService("HeapAnalyzerService"), CoroutineScope by MainScope()  {

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
        onAnalysis(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        heapAnalyzerNotification.stopForeground()
    }

    override fun onHandleIntent(intent: Intent?) {
        val hprofFile = intent?.getSerializableExtra(PARAM_HPROF_FILE) ?: return
        hprofFile as File
        val heapAnalysis = HprofDumper.analyze(hprofFile) {
            onAnalysis(it)
        }
        if (Leak.analyzeListener == null) {
            Ponlog.w { heapAnalysis.toFormatString() }
        } else {
            launch {
                Leak.analyzeListener?.onAnalysis(heapAnalysis.toFormatString())
            }
        }
    }

    private fun onAnalysis(step: OnAnalysisProgressListener.Step?) = launch {
        heapAnalyzerNotification.startForeground(step.percent, step.desc)
        Leak.analyzeListener?.onProgress(step.percent, step.desc)
    }

    private val OnAnalysisProgressListener.Step?.percent: Float
        get() {
            this ?: return -1F
            val index = this.ordinal
            val size = OnAnalysisProgressListener.Step.values().size
            return index.toFloat() / size.toFloat()
        }

    private val OnAnalysisProgressListener.Step?.desc: String
        get() {
            this ?: return "Waiting analyze"
            return when (this) {
                OnAnalysisProgressListener.Step.PARSING_HEAP_DUMP -> "Parsing heap dump"
                OnAnalysisProgressListener.Step.EXTRACTING_METADATA -> "Extracting metadata"
                OnAnalysisProgressListener.Step.FINDING_RETAINED_OBJECTS -> "Finding retained objects"
                OnAnalysisProgressListener.Step.FINDING_PATHS_TO_RETAINED_OBJECTS -> "Finding paths to retained objects"
                OnAnalysisProgressListener.Step.FINDING_DOMINATORS -> "Finding dominators"
                OnAnalysisProgressListener.Step.COMPUTING_NATIVE_RETAINED_SIZE -> "Computing native retained size"
                OnAnalysisProgressListener.Step.COMPUTING_RETAINED_SIZE -> "Computing retained size"
                OnAnalysisProgressListener.Step.BUILDING_LEAK_TRACES -> "Building leak traces"
                OnAnalysisProgressListener.Step.REPORTING_HEAP_ANALYSIS -> "Reporting heap analysis"
            }
        }
}