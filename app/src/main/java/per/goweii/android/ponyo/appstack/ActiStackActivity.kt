package per.goweii.android.ponyo.appstack

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_acti_stack.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.appstack.ActivityStackUpdateListener
import per.goweii.ponyo.appstack.AppStack

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
        Log.d("Ponyo-AppStack", AppStack.activityStack.copyStack().toString())
    }
}