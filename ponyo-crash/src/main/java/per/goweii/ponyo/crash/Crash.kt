package per.goweii.ponyo.crash

import android.app.Application
import android.os.Handler
import android.os.Looper
import per.goweii.ponyo.log.Ponlog

class Crash {
    companion object {
        private var application: Application? = null

        fun install(application: Application) {
            this.application = application
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            val crashHandler = CrashHandler(application, defaultHandler)
            Thread.setDefaultUncaughtExceptionHandler(crashHandler)
            Handler(Looper.getMainLooper()).post {
                while (true) {
                    try {
                        Looper.loop()
                    } catch (e: Throwable) {
                        crashHandler.uncaughtException(Looper.getMainLooper().thread, e)
                    }
                }
            }
        }
    }
}