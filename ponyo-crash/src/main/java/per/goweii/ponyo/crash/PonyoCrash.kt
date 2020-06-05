package per.goweii.ponyo.crash

import android.app.Application
import android.os.Handler
import android.os.Looper
import per.goweii.ponyo.log.Ponlog

class PonyoCrash {
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

class CrashHandler(
    private val application: Application,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {
    private val logger = Ponlog.create().apply {
        setAndroidLogPrinterEnable(true)
        setFileLogPrinterEnable(true)
    }

    private val activityLiftMethodNames = arrayOf(
        "handleLaunchActivity",
        "handleStartActivity",
        "handleResumeActivity"
    )

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.e { e }
        for (element in e.stackTrace) {
            if (element.className == "android.app.ActivityThread") {
                for (activityLiftMethodName in activityLiftMethodNames) {
                    if (element.methodName == activityLiftMethodName) {
                        defaultHandler?.uncaughtException(Looper.getMainLooper().thread, e)
                        defaultHandler?.uncaughtException(t, e)
                        CrashActivity.start(application, e)
                    }
                }
            }
        }
    }
}