package per.goweii.ponyo.appstack

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlin.text.StringBuilder

object ActivityStack : Application.ActivityLifecycleCallbacks {
    private var activityStackUpdateListeners = arrayListOf<() -> Unit>()
    private var appStateChangeListeners = arrayListOf<OnAppStateChangeListener>()

    val activityInfos = arrayListOf<ActivityInfo>()
    private var startCount = 0
    private var resumeCount = 0

    fun registerStackUpdateListener(listener: () -> Unit) {
        activityStackUpdateListeners.add(listener)
    }

    fun unregisterStackUpdateListener(listener: () -> Unit) {
        activityStackUpdateListeners.remove(listener)
    }

    fun registerOnAppStateChangeListener(listener: OnAppStateChangeListener) {
        appStateChangeListeners.add(listener)
    }

    fun unregisterOnAppStateChangeListener(listener: OnAppStateChangeListener) {
        appStateChangeListeners.remove(listener)
    }

    fun copyStack(): StringBuilder {
        fun getFragStack(
            fragmentInfo: FragmentInfo,
            isLast: Boolean,
            prefix: StringBuilder
        ): StringBuilder {
            val sbf = StringBuilder()
            sbf.append(prefix)
            if (!isLast) sbf.append("|—")
            else sbf.append("\\—")
            sbf.append(fragmentInfo.fragment.toString())
            sbf.append("\n")
            fragmentInfo.fragmentStack.fragmentInfos.forEachIndexed { i, info ->
                val prefix2 = StringBuilder(prefix)
                if (!isLast) prefix2.append("|")
                else prefix2.append(" ")
                prefix2.append(" ")
                val lastf = i == fragmentInfo.fragmentStack.fragmentInfos.size - 1
                sbf.append(getFragStack(info, lastf, prefix2))
            }
            return sbf
        }

        val sba = StringBuilder()
        activityInfos.forEachIndexed { ai, activityInfo ->
            val lasta = ai == activityInfos.size - 1
            if (!lasta) sba.append("|—")
            else sba.append("\\—")
            sba.append(activityInfo.activity.toString())
            sba.append("\n")
            activityInfo.fragmentStack.fragmentInfos.forEachIndexed { fi, fragmentInfo ->
                val prefix1 = StringBuilder()
                if (!lasta) prefix1.append("|")
                else prefix1.append(" ")
                prefix1.append(" ")
                val lastf = fi == activityInfo.fragmentStack.fragmentInfos.size - 1
                sba.append(getFragStack(fragmentInfo, lastf, prefix1))
            }
        }
        return sba
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
        startCount++
        if (startCount == 1) {
           notifyAppOnStart()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        resumeCount++
        if (resumeCount == 1) {
            notifyAppOnResume()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumeCount--
        if (resumeCount == 0) {
            notifyAppOnPause()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        startCount--
        if (startCount == 0) {
            notifyAppOnStop()
        }
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

    private fun notifyAppOnStart() {
        appStateChangeListeners.forEach { it.onStart() }
    }

    private fun notifyAppOnResume() {
        appStateChangeListeners.forEach { it.onResume() }
    }

    private fun notifyAppOnPause() {
        appStateChangeListeners.forEach { it.onPause() }
    }

    private fun notifyAppOnStop() {
        appStateChangeListeners.forEach { it.onStop() }
    }

}