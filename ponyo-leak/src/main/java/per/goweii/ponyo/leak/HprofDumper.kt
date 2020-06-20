package per.goweii.ponyo.leak

import android.app.Application
import android.os.Debug
import androidx.annotation.WorkerThread
import kotlinx.coroutines.*
import shark.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal object HprofDumper : CoroutineScope by MainScope() {

    @JvmStatic
    fun dump(
        application: Application,
        callback: (hprofFile: File) -> Unit
    ) = launch {
        val hprofFile = withContext(Dispatchers.IO) {
            val dir = File(application.filesDir, "leak")
            dir.mkdirs()
            dir.listFiles()?.forEach { it.delete() }
            val fileName =
                SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS'.hprof'", Locale.CHINA).format(Date())
            val hprofFile = File(dir, fileName)
            try {
                Debug.dumpHprofData(hprofFile.absolutePath)
            } catch (e: Throwable) {
            }
            hprofFile
        }
        callback.invoke(hprofFile)
    }

    @WorkerThread
    @JvmStatic
    fun analyze(
        hprof: File,
        listener: (step: OnAnalysisProgressListener.Step) -> Unit
    ): HeapAnalysis {
        return HeapAnalyzer(object : OnAnalysisProgressListener {
            override fun onAnalysisProgress(step: OnAnalysisProgressListener.Step) {
                listener(step)
            }
        }).analyze(
            heapDumpFile = hprof,
            leakingObjectFinder = WatchedRefFinder,
            referenceMatchers = AndroidReferenceMatchers.appDefaults,
            computeRetainedHeapSize = true,
            objectInspectors = AndroidObjectInspectors.appDefaults,
            metadataExtractor = AndroidMetadataExtractor,
            proguardMapping = null
        )
    }

}