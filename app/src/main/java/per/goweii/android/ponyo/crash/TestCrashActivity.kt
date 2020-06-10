package per.goweii.android.ponyo.crash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.*
import per.goweii.android.ponyo.R
import kotlin.concurrent.thread

class TestCrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        btn_throw_error_on_ui.setOnClickListener {
            throw RuntimeException("click btn error on ui")
        }
        btn_throw_error_on_io.setOnClickListener {
            thread {
                throw RuntimeException("click btn error on thread")
            }
        }
        btn_start_on_create_error_activity.setOnClickListener {
            startActivity(Intent(this, CrashOnCreateActivity::class.java))
        }
        btn_start_on_draw_error_activity.setOnClickListener {
            startActivity(Intent(this, CrashOnDrawActivity::class.java))
        }
        btn_add_on_draw_error_view.setOnClickListener {
            val view = CrashOnDrawView(this)
            fl_add_on_draw_error_view.addView(view)
        }
    }
}
