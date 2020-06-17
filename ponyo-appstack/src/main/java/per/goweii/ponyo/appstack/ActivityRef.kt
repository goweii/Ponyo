package per.goweii.ponyo.appstack

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

class ActivityRef(
    activity: Activity,
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