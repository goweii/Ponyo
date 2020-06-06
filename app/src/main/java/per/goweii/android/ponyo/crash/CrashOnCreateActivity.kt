package per.goweii.android.ponyo.crash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.*
import per.goweii.android.ponyo.R
import kotlin.concurrent.thread

class CrashOnCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        throw RuntimeException("onCreate error")
    }
}
