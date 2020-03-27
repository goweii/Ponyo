package per.goweii.ponyo.activitystack

import android.app.Activity

data class ActivityInfo(
    val activity: Activity,
    val fragmentStack: FragmentStack
)