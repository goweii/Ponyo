package per.goweii.ponyo.panel.tm

import android.app.Activity
import per.goweii.ponyo.timemonitor.TimeMonitor
import per.goweii.ponyo.utils.objectSimpleName

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
sealed class TM(val name: String) {
    fun start(tag: String) = TimeMonitor.start(name, tag)
    fun record(tag: String) = TimeMonitor.record(name, tag)
    fun end(tag: String) = TimeMonitor.end(name, tag)

    object APP_STARTUP : TM("Start Application")
    class ACTIVITY_STARTUP(activity: Activity) : TM("Start: ${activity.objectSimpleName}")
    class ACTIVITY_FINISH(activity: Activity) : TM("Finish: ${activity.objectSimpleName}")
}