package per.goweii.ponyo.leak

import android.app.Application

object Leak {

    fun initialize(application: Application) {
        LeakWatcher.initialize(application)
    }

}