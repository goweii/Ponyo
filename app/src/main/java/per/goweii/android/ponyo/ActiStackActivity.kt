package per.goweii.android.ponyo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_acti_stack.*
import per.goweii.ponyo.activitystack.ActivityStack

class ActiStackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acti_stack)

        ActivityStack.registerStackUpdateListener {
            tv_activity_stack_log.text = ActivityStack.copyStack()
        }

        supportFragmentManager.apply {
            beginTransaction().apply {
                replace(
                    R.id.fl_container,
                    ActiStackFragment()
                )
            }.commit()
        }
    }
}