package per.goweii.android.ponyo.activitystack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_acti_stack.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.activitystack.ActivityStack

class ActiStackActivity : AppCompatActivity(), () -> Unit {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acti_stack)

        ActivityStack.registerStackUpdateListener(this)

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
        ActivityStack.unregisterStackUpdateListener(this)
    }

    override fun invoke() {
        tv_activity_stack_log.text = ActivityStack.copyStack()
    }
}