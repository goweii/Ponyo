package per.goweii.ponyo.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

internal class ActivityStarter : Application.ActivityLifecycleCallbacks {

    companion object {
        fun register(application: Application): ActivityStarter {
            return ActivityStarter().also {
                application.registerActivityLifecycleCallbacks(it)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Starter.initializeFollowActivity(activity.javaClass)
        if (activity is FragmentActivity) {
            FragmentStarter.register(activity.supportFragmentManager)
        }
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
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
}