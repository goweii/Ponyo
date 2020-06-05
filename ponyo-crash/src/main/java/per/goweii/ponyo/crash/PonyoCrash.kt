package per.goweii.ponyo.crash

import android.app.Application
import android.os.Handler
import android.os.Looper
import per.goweii.ponyo.log.Ponlog

class PonyoCrash {
    companion object {
        private var application: Application? = null
        private val logger by lazy {
            Ponlog.create().apply {
                setAndroidLogPrinterEnable(true)
                setFileLogPrinterEnable(true)
            }
        }

        fun install(application: Application) {
            this.application = application
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            val threadCrashHandler = ThreadCrashHandler(application, logger, defaultHandler)
            Thread.setDefaultUncaughtExceptionHandler(threadCrashHandler)
            val uiCrashHandler = UICrashHandler(application, logger)
            Handler(Looper.getMainLooper()).post {
                while (true) {
                    try {
                        Looper.loop()
                    } catch (e: Throwable) {
                        uiCrashHandler.handleException(e)
                    }
                }
            }
        }
    }
}

class UICrashHandler(
    private val application: Application,
    private val logger: Ponlog.Logger
) {
    private val activityLiftMethodNames = arrayOf(
        "handleLaunchActivity",
        "handleStartActivity",
        "handleResumeActivity"
    )

    fun handleException(e: Throwable) {
        logger.e { e }
        for (element in e.stackTrace) {
            if (element.className == "android.app.ActivityThread") {
                for (activityLiftMethodName in activityLiftMethodNames) {
                    if (element.methodName == activityLiftMethodName) {
                        System.exit(0)
                        CrashActivity.start(application, e)
                    }
                }
            }
        }
    }
}

class ThreadCrashHandler(
    private val application: Application,
    private val logger: Ponlog.Logger,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.e { e }
        //defaultHandler?.uncaughtException(t, e)
        CrashActivity.start(application, e)
    }
}