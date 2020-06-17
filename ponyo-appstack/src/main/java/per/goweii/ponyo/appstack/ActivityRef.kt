package per.goweii.ponyo.appstack

import android.app.Activity
import java.lang.ref.WeakReference

data class ActivityRef(
    private val activity: Activity,
    val fragmentStack: FragmentStack?
) : WeakReference<Activity>(activity) {
    companion object {
        fun from(activity: Activity): ActivityRef {
            val fragmentStack = if (activity is FragmentActivity) {
                FragmentStack.create(activity.supportFragmentManager)
            } else {
                null
            }
            return ActivityRef(activity, fragmentStack)
        }
    }
}