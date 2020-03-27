package per.goweii.android.ponyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import per.goweii.android.ponyo.log.LogActivity
import per.goweii.android.ponyo.timemonitor.TM
import per.goweii.android.ponyo.timemonitor.TimeMonitorActivity
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.timemonitor.TimeMonitor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_print_log.setOnClickListener {
            startActivity(Intent(this@MainActivity, LogActivity::class.java))
        }
        tv_time_monitor.setOnClickListener {
            TM.START_ACTIVITY.start()
            startActivity(Intent(this@MainActivity, TimeMonitorActivity::class.java))
        }
        tv_activity_stack.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActiStackActivity::class.java))
        }
    }
}
