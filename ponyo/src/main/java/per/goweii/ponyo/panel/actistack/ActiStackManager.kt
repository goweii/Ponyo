package per.goweii.ponyo.panel.actistack

import android.widget.TextView
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.ActivityStackUpdateListener

object ActiStackManager: ActivityStackUpdateListener{

    private var tv_actistack: TextView? = null

    fun attach(tv: TextView){
        tv_actistack = tv
        tv.text = ActivityStack.copyStack()
    }

    override fun onStackUpdate() {
        tv_actistack?.text = ActivityStack.copyStack()
    }
}