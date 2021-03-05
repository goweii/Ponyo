package per.goweii.ponyo.panel.tm

import android.app.Activity
import android.text.TextUtils
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import per.goweii.ponyo.appstack.ActivityLifecycleListener
import per.goweii.ponyo.timemonitor.TimeLineEndListener
import java.util.*

object TmManager : TimeLineEndListener, ActivityLifecycleListener {
    private var tmAdapter: TmAdapter? = null
    private val datas = arrayListOf<TmEntity>()

    fun attach(tmAdapter: TmAdapter) {
        this.tmAdapter = tmAdapter
        tmAdapter.set(datas)
    }

    override fun onEnd(lineTag: String, lineInfo: String) {
        val tmEntity = TmEntity(lineTag, lineInfo)
        datas.find { it == tmEntity }
            ?.also { it.lineInfo = lineInfo }
            ?: let { datas.add(tmEntity) }
        tmAdapter?.set(datas)
    }

    override fun onCreated(activity: Activity) {
        TM.APP_STARTUP.record("${activity.tmTag()} onCreated")
        TM.ACTIVITY_STARTUP(activity).start("onCreated")
        activity.window.decorView.doOnLayout {
            TM.APP_STARTUP.record("${activity.tmTag()} onLayout")
            TM.ACTIVITY_STARTUP(activity).record("onLayout")
        }
        activity.window.decorView.doOnPreDraw {
            TM.APP_STARTUP.end("${activity.tmTag()} onPreDraw")
            TM.ACTIVITY_STARTUP(activity).end("onPreDraw")
        }
    }

    override fun onStarted(activity: Activity) {
        TM.APP_STARTUP.record("${activity.tmTag()} onStarted")
        TM.ACTIVITY_STARTUP(activity).record("onStarted")
    }

    override fun onResumed(activity: Activity) {
        TM.APP_STARTUP.record("${activity.tmTag()} onResumed")
        TM.ACTIVITY_STARTUP(activity).record("onResumed")
    }

    override fun onPaused(activity: Activity) {
    }

    override fun onStopped(activity: Activity) {
    }

    override fun onDestroyed(activity: Activity) {
        val tm = TM.ACTIVITY_STARTUP(activity)
        val datasIterator = datas.iterator()
        while (datasIterator.hasNext()) {
            val next = datasIterator.next()
            if (TextUtils.equals(next.lineTag, tm.name)) {
                datasIterator.remove()
            }
        }
        tmAdapter?.set(datas)
    }

    private fun Activity.tmTag(): String {
        return "${this::class.java.simpleName}(${Objects.hashCode(this)})"
    }

}