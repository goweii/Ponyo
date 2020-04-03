package per.goweii.ponyo.panel.actistack

import android.widget.TextView
import per.goweii.ponyo.appstack.ActivityStack

object ActiStackManager: () -> Unit {

    private var tv_actistack: TextView? = null

    override fun invoke() {
        tv_actistack?.text = ActivityStack.copyStack()
    }

    fun attach(tv: TextView){
        tv_actistack = tv
        tv.text = ActivityStack.copyStack()
    }
}