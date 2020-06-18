package per.goweii.ponyo.leak

import android.app.Application

object Leak {

    private lateinit var application: Application
    private lateinit var hprofDumper: HprofDumper

    internal var leakListener: LeakListener? = null

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        this.hprofDumper = HprofDumper(application)
        LeakWatcher.initialize(application)
    }

    fun setLeakListener(leakListener: LeakListener?) {
        this.leakListener = leakListener
    }

    fun dump() {
        hprofDumper.dump()
    }

    interface LeakListener {
        fun onLeak()
    }

}