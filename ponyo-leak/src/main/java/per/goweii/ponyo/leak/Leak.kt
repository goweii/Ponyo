package per.goweii.ponyo.leak

import android.app.Application

object Leak {

    internal var leakListener: LeakListener? = null

    fun initialize(application: Application) {
        LeakWatcher.initialize(application)
    }

    fun setLeakListener(leakListener: LeakListener?) {
        this.leakListener = leakListener
    }

    interface LeakListener {
        fun onLeak()
    }

}