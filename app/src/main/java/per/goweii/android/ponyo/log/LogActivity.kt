package per.goweii.android.ponyo.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_main.tv_print_log
import per.goweii.android.ponyo.R
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog

class LogActivity : AppCompatActivity() {

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        Ponlog.addLogPrinter(object : LogPrinter {
            override fun print(level: Ponlog.Level, tag: String, msg: String) {
                if (logStringBuilder.isNotEmpty()) {
                    logStringBuilder.append("\n")
                }
                logStringBuilder.append("[${level.name}]$tag:$msg")
                tvLogBoard.text = logStringBuilder
            }
        })

        tv_print_log.setOnClickListener {
            Ponlog.d("Intent") { intent }
        }
    }
}