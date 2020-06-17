package per.goweii.ponyo.appstack

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class ActivityStack private constructor() {
    private val activityLifecycle = ActivityLifecycle()

    private var activityStackUpdateListeners = arrayListOf<ActivityStackUpdateListener>()
    private var activityLifecycleListeners = arrayListOf<ActivityLifecycleListener>()

    val activities = arrayListOf<ActivityRef>()
        get() {
            val iterator = field.iterator()
            while (iterator.hasNext()) {
                val activityInfo = iterator.next()
                if (activityInfo.get() == null) {
                    iterator.remove()
                }
            }
            return field
        }

    companion object {
        internal fun create(application: Application): ActivityStack {
            val activityStack = ActivityStack()
            application.registerActivityLifecycleCallbacks(activityStack.activityLifecycle)
            return activityStack
        }
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

    fun copyStack(): StringBuilder {
        fun getFragStack(
            fragmentRef: FragmentRef,
            isLast: Boolean,
            prefix: StringBuilder
        ): StringBuilder {
            val sbf = StringBuilder()
            sbf.append(prefix)
            if (!isLast) sbf.append("|—")
            else sbf.append("\\—")
            sbf.append(fragmentRef.get().nameWithHex())
            sbf.append("\n")
            fragmentRef.fragmentStack.fragments.forEachIndexed { i, info ->
                val prefix2 = StringBuilder(prefix)
                if (!isLast) prefix2.append("|")
                else prefix2.append(" ")
                prefix2.append(" ")
                val lastf = i == fragmentRef.fragmentStack.fragments.size - 1
                sbf.append(getFragStack(info, lastf, prefix2))
            }
            return sbf
        }

        val sba = StringBuilder()
        activities.forEachIndexed { ai, activityInfo ->
            val lasta = ai == activities.size - 1
            if (!lasta) sba.append("|—")
            else sba.append("\\—")
            sba.append(activityInfo.get().nameWithHex())
            sba.append("\n")
            activityInfo.fragmentStack?.fragments?.forEachIndexed { fi, fragmentInfo ->
                val prefix1 = StringBuilder()
                if (!lasta) prefix1.append("|")
                else prefix1.append(" ")
                prefix1.append(" ")
                val lastf = fi == activityInfo.fragmentStack.fragments.size - 1
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

    private inner class ActivityLifecycle : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            val ref = ActivityRef.from(activity)
            ref.fragmentStack?.fragmentStackUpdateListener = {
                notifyStackUpdate()
            }
            activities.add(ref)
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
            var activityRef: ActivityRef? = null
            for (i in activities.size - 1 downTo 0) {
                val ref = activities[i]
                if (ref.get() == activity) {
                    activityRef = ref
                    break
                }
            }
            activityRef?.let { ref ->
                activities.remove(ref)
                ref.fragmentStack?.fragmentStackUpdateListener = null
                if (activity is FragmentActivity) {
                    ref.fragmentStack?.recycle(activity.supportFragmentManager)
                }
                ref.clear()
            }
            notifyStackUpdate()
            activityLifecycleListeners.forEach { it.onDestroyed(activity) }
        }
    }

}