package per.goweii.android.ponyo.crash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.*
import per.goweii.android.ponyo.R
import kotlin.concurrent.thread

class CrashOnDrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        val view = CrashOnDrawView(this)
        fl_add_on_draw_error_view.addView(view)
    }
}
