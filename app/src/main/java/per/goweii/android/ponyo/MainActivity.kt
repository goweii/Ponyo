package per.goweii.android.ponyo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import per.goweii.ponyo.log.Ponlog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_print_log.setOnClickListener {
            LogInnerClass().log()
        }
    }

    private fun log() {
        Ponlog.v("MainActivityTag") { "ponlog print msg" }
    }

    inner class LogInnerClass() {
        fun log() {
            Ponlog.v("MainActivityTag") { "ponlog print msg" }
        }
    }
}
