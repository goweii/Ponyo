package per.goweii.ponyo.panel.tm

import android.app.Activity
import android.text.TextUtils
import android.view.ViewTreeObserver
import per.goweii.ponyo.appstack.ActivityLifecycleListener
import per.goweii.ponyo.timemonitor.TimeLineEndListener

object TmManager : TimeLineEndListener, ActivityLifecycleListener {

    private var tmAdapter: TmAdapter? = null
    private val datas = arrayListOf<TmEntity>()

    fun attach(tmAdapter: TmAdapter) {
        this.tmAdapter = tmAdapter
        tmAdapter.set(datas)
    }

    override fun onEnd(lineTag: String, lineInfo: String) {
        var item: TmEntity? = null
        datas.forEach {
            if (TextUtils.equals(it.lineTag, lineTag)) {
                item = it
                return@forEach
            }
        }
        item?.let {
            it.lineInfo = lineInfo
        } ?: let {
            val tmEntity = TmEntity(lineTag, lineInfo)
            datas.add(tmEntity)
        }
        tmAdapter?.set(datas)
    }

    override fun onCreated(activity: Activity) {
        TM.APP_STARTUP.record("activity created")
        activity.window.decorView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                activity.window.decorView.viewTreeObserver.removeOnPreDrawListener(this)
                TM.APP_STARTUP.end("activity draw")
                return true
            }
        })
    }

    override fun onStarted(activity: Activity) {
        TM.APP_STARTUP.record("activity started")
    }

    override fun onResumed(activity: Activity) {
        TM.APP_STARTUP.record("activity resumed")
    }

    override fun onPaused(activity: Activity) {
    }

    override fun onStopped(activity: Activity) {
    }

    override fun onDestroyed(activity: Activity) {
    }

}