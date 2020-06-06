package per.goweii.ponyo.crash

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Looper
import android.os.Process
import android.view.Choreographer
import per.goweii.ponyo.log.Ponlog

/**
 * @author CuiZhen
 * @date 2020/6/6
 */
class CrashHandler(
    private val application: Application,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    private val logger = Ponlog.create().apply {
        setAndroidLogPrinterEnable(true)
        setFileLogPrinterEnable(true)
    }

    private val activityLifecycleMethodNames = arrayOf(
        "handleLaunchActivity",
        "handleStartActivity",
        "handleResumeActivity",
        "handlePauseActivity",
        "handleStopActivity",
        "handleDestroyActivity"
    )

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.e { e }
        if (t == Looper.getMainLooper().thread) {
            for (element in e.stackTrace) {
                when (element.className) {
                    "android.app.ActivityThread" -> {
                        if (activityLifecycleMethodNames.contains(element.methodName)) {
                            if (!tryFinishCauseActivityOnLifecycle(t, e)) {
                                exitAndStartCrashActivity(t, e)
                            }
                            return
                        }
                    }
                    Choreographer::class.java.name -> {
                        if (element.methodName == "doFrame") {
                            exitAndStartCrashActivity(t, e)
                            return
                        }
                    }
                }
            }
        }
    }

    private fun exitAndStartCrashActivity(t: Thread, e: Throwable) {
        CrashActivity.start(application, e)
        val iterator = activityStacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().finish()
            iterator.remove()
        }
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    private fun tryFinishCauseActivityOnLifecycle(t: Thread, e: Throwable): Boolean {
        fun findCauseActivity(throwable: Throwable): Activity? {
            for (element in throwable.stackTrace) {
                for (i in (activityStacks.size - 1) downTo 0) {
                    val activity = activityStacks[i]
                    if (activity.javaClass.name == element.className) {
                        return activity
                    }
                }
            }
            val cause = throwable.cause ?: return null
            if (cause == throwable) return null
            return findCauseActivity(cause)
        }
        return findCauseActivity(e)?.let {
            it.finish()
            true
        } ?: false
    }

    private val activityStacks = mutableListOf<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStacks.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStacks.remove(activity)
    }
}