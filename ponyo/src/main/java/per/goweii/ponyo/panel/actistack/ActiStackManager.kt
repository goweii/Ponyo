package per.goweii.ponyo.panel.actistack

import android.app.Activity
import android.view.ViewTreeObserver
import android.widget.TextView
import per.goweii.ponyo.appstack.ActivityLifecycleListener
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.ActivityStackUpdateListener
import per.goweii.ponyo.panel.tm.TM

object ActiStackManager: ActivityStackUpdateListener, ActivityLifecycleListener {

    private var tv_actistack: TextView? = null

    fun attach(tv: TextView){
        tv_actistack = tv
        tv.text = ActivityStack.copyStack()
    }

    override fun onStackUpdate() {
        tv_actistack?.text = ActivityStack.copyStack()
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