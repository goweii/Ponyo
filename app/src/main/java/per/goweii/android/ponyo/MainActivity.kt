package per.goweii.android.ponyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.timemonitor.TimeMonitor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_print_log.setOnClickListener {
            Ponlog.d("Intent") { intent }
        }
        tv_time_monitor.setOnClickListener {
            TimeMonitor.start("TimeMonitor")
            for (i in 0..100000) {
                val j = i % 10000
                if (j == 0) {
                    TimeMonitor.record("TimeMonitor", "for i=$i")
                }
            }
            TimeMonitor.end("TimeMonitor")
        }
        tv_activity_stack.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActiStackActivity::class.java))
        }
    }
}
