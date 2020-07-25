package per.goweii.ponyo.startup

import android.app.Application
import per.goweii.ponyo.startup.annotation.InitMeta

/**
 * @author CuiZhen
 * @date 2020/7/25
 */
internal class FollowStarter(
    private val application: Application
) {
    private val activityInitNames = mutableMapOf<String, ArrayList<String>>()
    private val fragmentInitNames = mutableMapOf<String, ArrayList<String>>()

    private var activityStarter: ActivityStarter? = null

    init {
        activityStarter = ActivityStarter.register(application)
    }

    internal fun addInitMeta(initMeta: InitMeta) {
        for (activity in initMeta.activities) {
            activityInitNames[activity]?.add(initMeta.className)
                ?: run { activityInitNames[activity] = arrayListOf(initMeta.className) }
        }
        for (fragment in initMeta.fragments) {
            fragmentInitNames[fragment]?.add(initMeta.className)
                ?: run { fragmentInitNames[fragment] = arrayListOf(initMeta.className) }
        }
    }

    internal fun getActivityInitNames(activityName: String): List<String>? {
        return activityInitNames[activityName]
    }

    internal fun getFragmentInitNames(activityName: String): List<String>? {
        return fragmentInitNames[activityName]
    }

    internal fun setInitialized(className: String) {
        val activityInitIt = activityInitNames.iterator()
        while (activityInitIt.hasNext()) {
            val entry = activityInitIt.next()
            entry.value.remove(className)
            if (entry.value.isEmpty()) {
                activityInitIt.remove()
            }
        }
        val fragmentInitIt = fragmentInitNames.iterator()
        while (fragmentInitIt.hasNext()) {
            val entry = fragmentInitIt.next()
            entry.value.remove(className)
            if (entry.value.isEmpty()) {
                fragmentInitIt.remove()
            }
        }
    }
}