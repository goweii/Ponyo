package per.goweii.ponyo.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

internal class ActivityStarter : Application.ActivityLifecycleCallbacks {

    companion object {
        fun create(application: Application): ActivityStarter {
            return ActivityStarter().also {
                application.registerActivityLifecycleCallbacks(it)
            }
        }
    }

    fun recycle(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Starter.initializeFollowActivity(activity.javaClass)
        if (activity is FragmentActivity) {
            FragmentStarter.create(activity.supportFragmentManager)
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