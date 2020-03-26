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
                sb.append("\t\t")
            }
            return sb.toString()
        }
        fun getFragStack(fragmentInfo: FragmentInfo, step: Int, isLast: Boolean): String {
            val sb = StringBuilder()
            sb.append(space(step))
            if (isLast) {
                sb.append("└")
            } else {
                sb.append("├")
            }
            sb.append(fragmentInfo.fragment.toString() + "\n")
            fragmentInfo.fragmentStack.fragmentInfos.forEachIndexed { i, info ->
                sb.append(getFragStack(info, step + 1, i == fragmentInfo.fragmentStack.fragmentInfos.size - 1))
            }
            return sb.toString()
        }
        val sb = StringBuilder()
        activityInfos.forEachIndexed { ai, activityInfo ->
            sb.append(space(0))
            if (ai == activityInfos.size - 1) {
                sb.append("└")
            } else {
                sb.append("├")
            }
            sb.append(activityInfo.activity.toString() + "\n")
            activityInfo.fragmentStack.fragmentInfos.forEachIndexed { fi, fragmentInfo ->
                sb.append(getFragStack(fragmentInfo, 1, fi == activityInfo.fragmentStack.fragmentInfos.size - 1))
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