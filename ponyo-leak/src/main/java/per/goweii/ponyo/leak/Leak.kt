package per.goweii.ponyo.leak

import android.app.Application
import java.lang.ref.WeakReference

object Leak {

    private lateinit var application: Application

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        this.application.registerActivityLifecycleCallbacks(ActivityStack)
    }

    private val Any.identity: String
        get() {
            val simpleName = this::class.qualifiedName
            val hexString = Integer.toHexString(System.identityHashCode(this))
            return "$simpleName@$hexString"
        }

    fun findLeakClass(): List<LeakInfo> {
        val leakInfos = arrayListOf<LeakInfo>()
        ActivityStack.activityInfos.forEach { activityInfo ->
            if (activityInfo.destroyed) {
                activityInfo.activityRef.get()?.let { activity ->
                    val objTag = activity.identity
                    val objRef = WeakReference<Any>(activity)
                    val leakInfo = LeakInfo(objTag, objRef)
                    leakInfos.add(leakInfo)
                }
            }
            activityInfo.fragmentStack.fragmentInfos.forEach { fragmentInfo ->
                if (fragmentInfo.destroyed) {
                    fragmentInfo.fragmentRef.get()?.let { fragment ->
                        val objTag = fragment.identity
                        val objRef = WeakReference<Any>(fragment)
                        val leakInfo = LeakInfo(objTag, objRef)
                        leakInfos.add(leakInfo)
                    }
                }
            }
        }
        return leakInfos
    }

}