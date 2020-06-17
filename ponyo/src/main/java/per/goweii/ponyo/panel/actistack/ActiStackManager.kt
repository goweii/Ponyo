package per.goweii.ponyo.panel.actistack

import android.widget.TextView
import per.goweii.ponyo.appstack.ActivityStackUpdateListener
import per.goweii.ponyo.appstack.AppStack

object ActiStackManager: ActivityStackUpdateListener{

    private var tv_actistack: TextView? = null

    fun attach(tv: TextView){
        tv_actistack = tv
        tv.text = AppStack.activityStack.copyStack()
    }

    override fun onStackUpdate() {
        tv_actistack?.text = AppStack.activityStack.copyStack()
    }
}