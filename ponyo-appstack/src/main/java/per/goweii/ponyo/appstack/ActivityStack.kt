package per.goweii.ponyo.appstack

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

object ActivityStack : Application.ActivityLifecycleCallbacks {
    private var activityStackUpdateListeners = arrayListOf<ActivityStackUpdateListener>()
    private var activityLifecycleListeners = arrayListOf<ActivityLifecycleListener>()

    val activityInfos = arrayListOf<ActivityInfo>()
        get() {
            val iterator = field.iterator()
            while (iterator.hasNext()) {
                val activityInfo = iterator.next()
                if (activityInfo.activityRef.get() == null) {
                    iterator.remove()
                }
            }
            return field
        }

    fun registerStackUpdateListener(listener: ActivityStackUpdateListener) {
        activityStackUpdateListeners.add(listener)
    }

    fun unregisterStackUpdateListener(listener: ActivityStackUpdateListener) {
        activityStackUpdateListeners.remove(listener)
    }

    fun registerActivityLifecycleListener(listener: ActivityLifecycleListener) {
        activityLifecycleListeners.add(listener)
    }

    fun unregisterActivityLifecycleListener(listener: ActivityLifecycleListener) {
        activityLifecycleListeners.remove(listener)
    }

    private fun Any?.nameWithHex(): String = this?.run {
        val simpleName = this::class.java.simpleName
        val hexString = Integer.toHexString(System.identityHashCode(this))
        return "$simpleName@$hexString"
    } ?: "Unknown@0"

    fun copyStack(includeLeaks: Boolean = false): StringBuilder {
        fun getFragStack(
            fragmentInfo: FragmentInfo,
            isLast: Boolean,
            prefix: StringBuilder
        ): StringBuilder {
            val sbf = StringBuilder()
            sbf.append(prefix)
            if (!isLast) sbf.append("|—")
            else sbf.append("\\—")
            sbf.append(fragmentInfo.fragmentRef.get().nameWithHex())
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
            sba.append(activityInfo.activityRef.get().nameWithHex())
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
            it.onStackUpdate()
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
        val activityInfo = ActivityInfo(WeakReference(activity), fragmentStack)
        activityInfos.add(activityInfo)
        notifyStackUpdate()
        activityLifecycleListeners.forEach { it.onCreated(activity) }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStarted(activity: Activity) {
        activityLifecycleListeners.forEach { it.onStarted(activity) }
    }

    override fun onActivityResumed(activity: Activity) {
        activityLifecycleListeners.forEach { it.onResumed(activity) }
    }

    override fun onActivityPaused(activity: Activity) {
        activityLifecycleListeners.forEach { it.onPaused(activity) }
    }

    override fun onActivityStopped(activity: Activity) {
        activityLifecycleListeners.forEach { it.onStopped(activity) }
    }

    override fun onActivityDestroyed(activity: Activity) {
        var activityInfo: ActivityInfo? = null
        for (i in activityInfos.size - 1 downTo 0) {
            val info = activityInfos[i]
            if (info.activityRef.get() == activity) {
                activityInfo = info
                break
            }
        }
        activityInfo?.let { info ->
            activityInfos.remove(info)
            info.fragmentStack.fragmentStackUpdateListener = null
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(info.fragmentStack)
            }
            info.activityRef.clear()
        }
        notifyStackUpdate()
        activityLifecycleListeners.forEach { it.onDestroyed(activity) }
    }

}