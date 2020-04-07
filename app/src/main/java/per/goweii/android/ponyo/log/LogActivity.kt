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

    private val tvLogBoard by lazy { tv_log_board }
    private val logStringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        Ponlog.addLogPrinter(this)

        cb_auto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startAutoLog()
            } else {
                stopAutoLog()
            }
        }

        tv_print_error.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    val user = newUser()
                    for (i in 0..1) {
                        val user1 = newUser()
                        for (j in 0..1) {
                            user1.friends.add(newUser())
                        }
                        user.friends.add(user1)
                    }
                    Ponlog.e("User") { user }
                }
            }
        }

        tv_print_warn.setOnClickListener {
            launch {
                val a1 = async(Dispatchers.Default) {
                    for (i in 0..100) {
                        Ponlog.e("User") { newUser() }
                    }
                }
                val a2 = async(Dispatchers.IO) {
                    for (i in 0..100) {
                        Ponlog.e("User") { newUser() }
                    }
                }
                a1.await()
                a2.await()
            }
        }

        tv_print_info.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Ponlog.log(Ponlog.Level.INFO, null, intent)
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
        log()
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

private data class User(
    val name: String,
    val age: Int,
    val height: Float,
    val friends: MutableList<User>
)

private fun newUser(): User {
    return User("zhangsan", 20, 180.1F, arrayListOf<User>())
}

fun log() {
    when (Random.nextInt(5)) {
        0 -> Ponlog.e("User") { newUser() }
        1 -> Ponlog.w("User") { newUser() }
        2 -> Ponlog.i("User") { newUser() }
        3 -> Ponlog.d("User") { newUser() }
        4 -> Ponlog.v("User") { newUser() }
    }
}
