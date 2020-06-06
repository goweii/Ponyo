package per.goweii.ponyo.crash

import android.app.Application
import android.os.Handler
import android.os.Looper
import per.goweii.ponyo.log.Ponlog

class Crash {
    companion object {
        fun initialize(application: Application) {
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (defaultHandler is CrashHandler) return
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