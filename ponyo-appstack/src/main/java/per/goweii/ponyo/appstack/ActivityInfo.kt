package per.goweii.ponyo.appstack

import android.app.Activity
import java.lang.ref.WeakReference

data class ActivityInfo(
    val activityRef: WeakReference<Activity>,
    val fragmentStack: FragmentStack
)