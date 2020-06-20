package per.goweii.ponyo.leak

import android.app.Application

object Leak {

    private lateinit var application: Application

    internal var leakListener: LeakListener? = null
    internal var analyzeListener: AnalyzeListener? = null

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        LeakWatcher.initialize(application)
    }

    fun setLeakListener(leakListener: LeakListener?) {
        this.leakListener = leakListener
    }

    fun setAnalyzeListener(analyzeListener: AnalyzeListener?) {
        this.analyzeListener = analyzeListener
    }

    fun dumpAndAnalyze() {
        HprofDumper.dump(application) {
            HeapAnalyzerService.start(application, it)
        }
    }

    interface LeakListener {
        fun onLeak(count: Int)
    }

    interface AnalyzeListener {
        fun onProgress(percent: Float, desc: String)
        fun onAnalysis(heapAnalysis: String)
    }

}