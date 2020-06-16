package per.goweii.ponyo.leak

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

internal object ActivityStack : Application.ActivityLifecycleCallbacks {

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

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val fragmentStack = FragmentStack()
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentStack, false)
        }
        val activityInfo = ActivityInfo(WeakReference(activity), fragmentStack)
        activityInfos.add(activityInfo)
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
        for (i in activityInfos.size - 1 downTo 0) {
            val info = activityInfos[i]
            if (info.activityRef.get() == activity) {
                info.destroyed = true
                break
            }
        }
    }

}