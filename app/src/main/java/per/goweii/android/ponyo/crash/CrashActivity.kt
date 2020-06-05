package per.goweii.android.ponyo.crash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.*
import per.goweii.android.ponyo.R
import kotlin.concurrent.thread

class CrashActivity : AppCompatActivity() {

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
        throw RuntimeException("onCreate error")
    }

    override fun onStop() {
        super.onStop()
    }
}
