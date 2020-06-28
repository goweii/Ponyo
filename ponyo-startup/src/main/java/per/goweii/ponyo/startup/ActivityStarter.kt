package per.goweii.ponyo.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityStarter(
    application: Application
) : Application.ActivityLifecycleCallbacks {
    private val map = mutableMapOf<String, ArrayList<String>>()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun add(activityName: String, initName: String): ActivityStarter {
        map[activityName]?.add(initName) ?: run {
            map[activityName] = arrayListOf(initName)
        }
        return this
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val activityName = activity::class.java.name
        map[activityName]?.let { list ->
            val initRunner = InitRunner()
            list.forEach {
                if (!Starter.isInitialized(it)) {
                    initRunner.add(it)
                }
            }
            initRunner.run()
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