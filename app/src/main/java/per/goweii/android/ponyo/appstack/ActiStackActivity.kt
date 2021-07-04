package per.goweii.android.ponyo.appstack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import per.goweii.android.ponyo.R

class ActiStackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acti_stack)
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