package per.goweii.android.ponyo.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_main.tv_print_log
import per.goweii.android.ponyo.R
import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import kotlin.random.Random

class LogActivity : AppCompatActivity(), LogPrinter {

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        Ponlog.addLogPrinter(this)

        tv_print_log.setOnClickListener {
            when (Random.nextInt(5)) {
                0 -> Ponlog.e("Intent") { intent }
                1 -> Ponlog.w("Intent") { intent }
                2 -> Ponlog.i("Intent") { intent }
                3 -> Ponlog.d("Intent") { intent }
                4 -> Ponlog.v("Intent") { intent }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Ponlog.removeLogPrinter(this)
    }

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        if (logStringBuilder.isNotEmpty()) {
            logStringBuilder.append("\n")
        }
        logStringBuilder.append("[${level.name}]$tag:$msg")
        tvLogBoard.text = logStringBuilder
    }
}