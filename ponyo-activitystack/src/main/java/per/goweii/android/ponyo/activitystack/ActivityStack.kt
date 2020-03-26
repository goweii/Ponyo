package per.goweii.android.ponyo.activitystack

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.StringBuilder

object ActivityStack : Application.ActivityLifecycleCallbacks {
    private var activityStackUpdateListeners = arrayListOf<() -> Unit>()

    val activityInfos = arrayListOf<ActivityInfo>()

    fun registerStackUpdateListener(listener: () -> Unit) {
        activityStackUpdateListeners.add(listener)
    }

    fun unregisterStackUpdateListener(listener: () -> Unit) {
        activityStackUpdateListeners.remove(listener)
    }

    fun copyStack(): String {
        fun space(count: Int): String {
            val sb = StringBuilder()
            for (i in 0 until count) {
                sb.append(" ")
            }
            return sb.toString()
        }
        fun getFragStack(fragmentInfo: FragmentInfo, step: Int): String {
            val sb = StringBuilder()
            sb.append(space(step) + fragmentInfo.fragment.toString() + "\n")
            for (info in fragmentInfo.fragmentStack.fragmentInfos) {
                sb.append(getFragStack(info, step + 1))
            }
            return sb.toString()
        }
        val sb = StringBuilder()
        for (activityInfo in activityInfos) {
            sb.append(space(0) + activityInfo.activity.toString() + "\n")
            for (fragmentInfo in activityInfo.fragmentStack.fragmentInfos) {
                sb.append(getFragStack(fragmentInfo, 1))
            }
        }
        return sb.toString()
    }

    private fun notifyStackUpdate() {
        activityStackUpdateListeners.forEach {
            it.invoke()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val fragmentStack = FragmentStack()
        fragmentStack.fragmentStackUpdateListener = {
            notifyStackUpdate()
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentStack, false)
        }
        val activityInfo = ActivityInfo(activity, fragmentStack)
        activityInfos.add(activityInfo)
        notifyStackUpdate()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        var activityInfo: ActivityInfo? = null
        for (i in activityInfos.size - 1 downTo 0) {
            val info = activityInfos[i]
            if (info.activity == activity) {
                activityInfo = info
                break
            }
        }
        activityInfo ?: return
        activityInfos.remove(activityInfo)
        activityInfo.fragmentStack.fragmentStackUpdateListener = null
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(activityInfo.fragmentStack)
        }
        notifyStackUpdate()
    }

}