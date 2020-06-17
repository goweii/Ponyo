package per.goweii.android.ponyo.appstack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_acti_stack.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.appstack.ActivityStackUpdateListener
import per.goweii.ponyo.appstack.AppStack
import per.goweii.ponyo.log.Ponlog

class ActiStackActivity : AppCompatActivity(), ActivityStackUpdateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acti_stack)

        AppStack.activityStack.registerStackUpdateListener(this)

        supportFragmentManager.apply {
            beginTransaction().apply {
                replace(
                    R.id.fl_container,
                    ActiStackFragment()
                )
            }.commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppStack.activityStack.unregisterStackUpdateListener(this)
    }

    override fun onStackUpdate() {
        tv_activity_stack_log.text = AppStack.activityStack.copyStack()
        Ponlog.d {AppStack.activityStack.copyStack() }
    }
}