package per.goweii.ponyo.leak

import android.app.Activity
import java.lang.ref.WeakReference

internal data class ActivityInfo(
    val activityRef: WeakReference<Activity>,
    val fragmentStack: FragmentStack,
    var destroyed: Boolean = false
)