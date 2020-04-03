package per.goweii.android.ponyo.timemonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.timemonitor.TimeLineEndListener
import per.goweii.ponyo.timemonitor.TimeMonitor

class TimeMonitorActivity : AppCompatActivity(), TimeLineEndListener {

    private val tvLogBoard by lazy { tv_log_board }

    override fun onCreate(savedInstanceState: Bundle?) {
        TimeMonitor.registerTimeLineEndListener(this)
        TM.START_ACTIVITY.record("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_monitor)
        TM.START_ACTIVITY.record("setContentView")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        TM.START_ACTIVITY.record("onWindowFocusChanged")
        TM.START_ACTIVITY.end()
    }

    override fun onDestroy() {
        super.onDestroy()
        TimeMonitor.unregisterTimeLineEndListener(this)
    }

    override fun onEnd(lineTag: String, lineInfo: String) {
        tvLogBoard.text = lineInfo
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