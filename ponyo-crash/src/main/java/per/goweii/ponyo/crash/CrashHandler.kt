package per.goweii.ponyo.crash

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.Choreographer
import android.widget.Toast
import java.lang.ref.WeakReference

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

    var customCrashActivity: Class<out Activity>? = null

    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val sp by lazy {
        application.getSharedPreferences("ponyo-crash", Context.MODE_PRIVATE)
    }

    private var lastKillProcessTime: Long
        get() {
            return sp.getLong("lastKillProcessTime", 0L)
        }
        set(value) {
            sp.edit().putLong("lastKillProcessTime", value).apply()
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
        e.printStackTrace()
        var shouldKillProcess = false
        if (t == Looper.getMainLooper().thread) {
            e.stackTrace.forEach { element ->
                when (element.className) {
                    "android.app.ActivityThread" -> {
                        if (activityLifecycleMethodNames.contains(element.methodName)) {
                            if (!tryFinishCauseActivityOnLifecycle(t, e)) {
                                shouldKillProcess = true
                            }
                            return@forEach
                        }
                    }
                    Choreographer::class.java.name -> {
                        if (element.methodName == "doFrame") {
                            shouldKillProcess = true
                            return@forEach
                        }
                    }
                }
            }
        }
        if (shouldKillProcess) {
            val currKill = System.currentTimeMillis()
            val lastKill = lastKillProcessTime
            lastKillProcessTime = currKill
            if (currKill - lastKill > 10_000) {
                exitAndStartCrashActivity(t, e)
            } else {
                defaultHandler?.uncaughtException(t, e)
            }
        } else {
            if (BuildConfig.DEBUG) {
                if (t == Looper.getMainLooper().thread) {
                    toastOnError()
                } else {
                    mainHandler.post { toastOnError() }
                }
            }
        }
    }

    private fun toastOnError() {
        Toast.makeText(application, "拦截到一个崩溃，请在日志查看详情", Toast.LENGTH_LONG).show()
    }

    private fun exitAndStartCrashActivity(t: Thread, e: Throwable) {
        customCrashActivity?.let {
            application.startActivity(Intent(application, it).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("error", e)
            })
        }
        val iterator = activityStacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.finish()
            iterator.remove()
        }
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    private fun tryFinishCauseActivityOnLifecycle(t: Thread, e: Throwable): Boolean {
        fun findCauseActivity(throwable: Throwable): Activity? {
            for (element in throwable.stackTrace) {
                for (i in (activityStacks.size - 1) downTo 0) {
                    val ref = activityStacks[i]
                    val activity = ref.get() ?: continue
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

    private val activityStacks = mutableListOf<WeakReference<Activity>>()
        get() {
            val iterator = field.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                if (ref.get() == null) {
                    iterator.remove()
                }
            }
            return field
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStacks.add(WeakReference(activity))
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
        for (i in activityStacks.size - 1 downTo 0) {
            val ref = activityStacks[i]
            if (ref.get() == activity) {
                activityStacks.removeAt(i)
                break
            }
        }
    }
}