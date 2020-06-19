package per.goweii.ponyo.leak

import android.app.Application

object Leak {

    private lateinit var application: Application

    internal var leakListener: LeakListener? = null

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        LeakWatcher.initialize(application)
    }

    fun setLeakListener(leakListener: LeakListener?) {
        this.leakListener = leakListener
    }

    fun dumpAndAnalyze() {
        HprofDumper.dump(application) {
            HeapAnalyzerService.start(application, it)
        }
    }

    interface LeakListener {
        fun onLeak()
    }

}