package per.goweii.ponyo.leak

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

internal class ActivityWatcher {

    private var fragmentXWatcher: FragmentXWatcher? = null
    private var fragmentOWatcher: FragmentOWatcher? = null

    private val activityLifecycle = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is FragmentActivity) {
                if (fragmentXWatcher == null) fragmentXWatcher = FragmentXWatcher()
                fragmentXWatcher?.watch(activity)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (fragmentOWatcher == null) fragmentOWatcher = FragmentOWatcher()
                    fragmentOWatcher?.watch(activity)
                }
            }
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
            LeakWatcher.watch(activity)
        }
    }

    fun watch(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycle)
    }

}