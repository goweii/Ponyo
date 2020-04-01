package per.goweii.android.ponyo.log

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_main.tv_print_log
import kotlinx.coroutines.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import kotlin.random.Random

class LogActivity : AppCompatActivity(), LogPrinter, CoroutineScope by MainScope() {

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        Ponlog.addLogPrinter(this)

        cb_auto.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startAutoLog()
            } else {
                stopAutoLog()
            }
        }

        tv_print_error.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.e("Intent") { intent }
                }
            }
        }

        tv_print_warn.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.w("Intent") { intent }
                }
            }
        }

        tv_print_info.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.i("Intent") { intent }
                }
            }
        }

        tv_print_debug.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.d("Intent") { intent }
                }
            }
        }

        tv_print_visible.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.v("Intent") { intent }
                }
            }
        }
    }

    private val autoHandler = Handler()

    @SuppressLint("HandlerLeak")
    private var autoRunnable = Runnable {
        when (Random.nextInt(5)) {
            0 -> Ponlog.e("Intent") { intent }
            1 -> Ponlog.w("Intent") { intent }
            2 -> Ponlog.i("Intent") { intent }
            3 -> Ponlog.d("Intent") { intent }
            4 -> Ponlog.v("Intent") { intent }
        }
        startAutoLog()
    }

    private fun startAutoLog() {
        autoHandler.postDelayed(autoRunnable, 200)
    }

    private fun stopAutoLog() {
        autoHandler.removeCallbacks(autoRunnable)
    }

    override fun onDestroy() {
        Ponlog.removeLogPrinter(this)
        cancel()
        stopAutoLog()
        super.onDestroy()
    }

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        launch {
            if (logStringBuilder.isNotEmpty()) {
                logStringBuilder.append("\n")
            }
            logStringBuilder.append("[${level.name}]$tag:$msg")
            tvLogBoard.text = logStringBuilder
        }
    }
}