package per.goweii.ponyo.panel.tm

import android.app.Activity
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.appstack.ActivityLifecycleListener
import per.goweii.ponyo.timemonitor.OnTimeLineEndListener
import per.goweii.ponyo.timemonitor.TimeLine
import per.goweii.ponyo.utils.objectSimpleName

object TmManager : OnTimeLineEndListener, ActivityLifecycleListener {
    private var rv: RecyclerView? = null
    private var tmAdapter: TmAdapter? = null
    private val datas = arrayListOf<TmEntity>()

    fun attach(rv: RecyclerView) {
        this.rv = rv
        rv.layoutManager = LinearLayoutManager(rv.context)
        tmAdapter = TmAdapter()
        rv.adapter = tmAdapter
        tmAdapter?.set(datas)
    }

    override fun onEnd(timeLine: TimeLine) {
        val tmEntity = TmEntity(timeLine)
        datas.remove(tmEntity)
        datas.add(tmEntity)
        val adapter = tmAdapter ?: return
        adapter.set(datas)
        if (adapter.itemCount > 0) {
            rv?.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onCreated(activity: Activity) {
        TM.APP_STARTUP.record("${activity.objectSimpleName} onCreated")
        TM.ACTIVITY_STARTUP(activity).start("onCreated")
        activity.window.decorView.doOnLayout {
            TM.APP_STARTUP.record("${activity.objectSimpleName} onLayout")
            TM.ACTIVITY_STARTUP(activity).record("onLayout")
        }
        activity.window.decorView.doOnPreDraw {
            TM.APP_STARTUP.end("${activity.objectSimpleName} onPreDraw")
            TM.ACTIVITY_STARTUP(activity).end("onPreDraw")
        }
    }

    override fun onStarted(activity: Activity) {
        TM.APP_STARTUP.record("${activity.objectSimpleName} onStarted")
        TM.ACTIVITY_STARTUP(activity).record("onStarted")
    }

    override fun onResumed(activity: Activity) {
        TM.APP_STARTUP.record("${activity.objectSimpleName} onResumed")
        TM.ACTIVITY_STARTUP(activity).record("onResumed")
    }

    override fun onPaused(activity: Activity) {
        if (activity.isFinishing) {
            TM.ACTIVITY_FINISH(activity).start("onPaused")
        }
    }

    override fun onStopped(activity: Activity) {
        if (activity.isFinishing) {
            TM.ACTIVITY_FINISH(activity).record("onStopped")
        }
    }

    override fun onDestroyed(activity: Activity) {
        if (activity.isFinishing) {
            TM.ACTIVITY_FINISH(activity).end("onDestroyed")
        }
    }

}