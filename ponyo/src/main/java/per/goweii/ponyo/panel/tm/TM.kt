package per.goweii.ponyo.panel.tm

import android.app.Activity
import per.goweii.ponyo.timemonitor.TimeMonitor
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
sealed class TM(
    val name: String
) {
    object APP_STARTUP : TM("APP_STARTUP")
    class ACTIVITY_STARTUP(activity: Activity) :
        TM("ACTIVITY_STARTUP: ${activity::class.java.simpleName}(${Objects.hashCode(activity)})")

    fun start(tag: String) {
        TimeMonitor.start(name, tag)
    }

    fun record(tag: String) {
        TimeMonitor.record(name, tag)
    }

    fun end(tag: String) {
        TimeMonitor.end(name, tag)
    }
}