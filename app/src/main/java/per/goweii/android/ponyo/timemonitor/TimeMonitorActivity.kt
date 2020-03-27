package per.goweii.android.ponyo.timemonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_main.tv_print_log
import per.goweii.android.ponyo.R
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.timemonitor.TimeMonitor

class TimeMonitorActivity : AppCompatActivity() {

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        TM.START_ACTIVITY.record("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_monitor)
        TM.START_ACTIVITY.record("setContentView")
        Ponlog.addLogPrinter(object : LogPrinter {
            override fun print(level: Ponlog.Level, tag: String, msg: String) {
                if (logStringBuilder.isNotEmpty()) {
                    logStringBuilder.append("\n")
                }
                logStringBuilder.append("[${level.name}]$tag:$msg")
                tvLogBoard.text = logStringBuilder
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        TM.START_ACTIVITY.record("onWindowFocusChanged=$hasFocus")
        TM.START_ACTIVITY.end()
    }
}

enum class TM {
    START_ACTIVITY;

    fun start() {
        TimeMonitor.start(name)
    }

    fun record(tagPoint: String) {
        TimeMonitor.record(name, tagPoint)
    }

    fun end() {
        TimeMonitor.end(name)
    }
}