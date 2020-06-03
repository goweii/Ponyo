package per.goweii.android.ponyo.log

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.coroutines.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import kotlin.random.Random

class LogActivity : AppCompatActivity(), LogPrinter, CoroutineScope by MainScope() {

    private data class User(
        val name: String,
        val age: Int,
        val height: Float,
        val friends: MutableList<User>
    )

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()
    private val logger = Ponlog.create().apply {
        setFileLogPrinterEnable(true)
        addLogPrinter(this@LogActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        cb_auto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startAutoLog()
            } else {
                stopAutoLog()
            }
        }

        tv_print_assert.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.a("User") { newUser() }
                }
            }
        }

        tv_print_error.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.e("User") { newUser() }
                }
            }
        }

        tv_print_warn.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.w("User") { newUser() }
                }
            }
        }

        tv_print_info.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.i(null) { intent }
                }
            }
        }

        tv_print_debug.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.d("Intent") { intent }
                }
            }
        }

        tv_print_visible.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    logger.v("Intent") { intent }
                }
            }
        }
    }

    private val autoHandler = Handler()

    @SuppressLint("HandlerLeak")
    private var autoRunnable = Runnable {
        if (Random.nextBoolean()) {
            when (Random.nextInt(6)) {
                0 -> logger.a("User") { newUser() }
                1 -> logger.e("User") { newUser() }
                2 -> logger.w("User") { newUser() }
                3 -> logger.i("User") { newUser() }
                4 -> logger.d("User") { newUser() }
                5 -> logger.v("User") { newUser() }
            }
        } else {
            when (Random.nextInt(6)) {
                0 -> logger.a("Intent") { intent }
                1 -> logger.e("Intent") { intent }
                2 -> logger.w("Intent") { intent }
                3 -> logger.i("Intent") { intent }
                4 -> logger.d("Intent") { intent }
                5 -> logger.v("Intent") { intent }
            }
        }
        startAutoLog()
    }

    private fun newUser(): User {
        return User("zhangsan", 20, 180.1F, arrayListOf<User>())
    }

    private fun startAutoLog() {
        autoHandler.postDelayed(autoRunnable, 1000)
    }

    private fun stopAutoLog() {
        autoHandler.removeCallbacks(autoRunnable)
    }

    override fun onDestroy() {
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
