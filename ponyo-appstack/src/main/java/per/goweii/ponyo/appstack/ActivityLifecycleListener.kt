package per.goweii.ponyo.appstack

import android.app.Activity

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
interface ActivityLifecycleListener {
    fun onCreated(activity: Activity)
    fun onStarted(activity: Activity)
    fun onResumed(activity: Activity)
    fun onPaused(activity: Activity)
    fun onStopped(activity: Activity)
    fun onDestroyed(activity: Activity)
}