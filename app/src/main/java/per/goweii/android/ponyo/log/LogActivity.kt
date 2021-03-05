package per.goweii.android.ponyo.log

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.coroutines.*
import per.goweii.android.ponyo.R
import per.goweii.android.ponyo.multiprocess.MultiProcessService1
import per.goweii.android.ponyo.multiprocess.MultiProcessService2
import kotlin.random.Random

class LogActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private data class User(
        val name: String,
        val age: Int,
        val height: Float,
        val friends: MutableList<User>
    )

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

        cb_service1.setOnCheckedChangeListener { _, isChecked ->
            val intent = Intent(this, MultiProcessService1::class.java)
            if (isChecked) {
                startService(intent)
            } else {
                stopService(intent)
            }
        }
        cb_service2.setOnCheckedChangeListener { _, isChecked ->
            val intent = Intent(this, MultiProcessService2::class.java)
            if (isChecked) {
                startService(intent)
            } else {
                stopService(intent)
            }
        }

        tv_print_10.setOnClickListener {
            for (i in 1..10) {
                Log.d("Num", i.toString())
            }
        }

        tv_print_assert.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.ASSERT, "User", newUser().toString())
                }
            }
        }

        tv_print_error.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.ERROR, "User", newUser().toString())
                }
            }
        }

        tv_print_warn.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.WARN, "User", newUser().toString())
                }
            }
        }

        tv_print_info.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.INFO, "User", intent.toString())
                }
            }
        }

        tv_print_debug.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.DEBUG, "User", intent.toString())
                }
            }
        }

        tv_print_visible.setOnClickListener {
            launch {
                withContext(Dispatchers.IO) {
                    Log.println(Log.VERBOSE, "User", intent.toString())
                }
            }
        }
    }

    private val autoHandler = Handler()

    @SuppressLint("HandlerLeak")
    private var autoRunnable = Runnable {
        if (Random.nextBoolean()) {
            when (Random.nextInt(6)) {
                0 -> Log.println(Log.ASSERT, "User", newUser().toString())
                1 -> Log.println(Log.ERROR, "User", newUser().toString())
                2 -> Log.println(Log.WARN, "User", newUser().toString())
                3 -> Log.println(Log.DEBUG, "User", newUser().toString())
                4 -> Log.println(Log.INFO, "User", newUser().toString())
                5 -> Log.println(Log.VERBOSE, "User", newUser().toString())
            }
        } else {
            when (Random.nextInt(6)) {
                0 -> Log.println(Log.ASSERT, "Intent", intent.toString())
                1 -> Log.println(Log.ERROR, "Intent", intent.toString())
                2 -> Log.println(Log.WARN, "Intent", intent.toString())
                3 -> Log.println(Log.DEBUG, "Intent", intent.toString())
                4 -> Log.println(Log.INFO, "Intent", intent.toString())
                5 -> Log.println(Log.VERBOSE, "Intent", intent.toString())
            }
        }
        startAutoLog()
    }

    private fun newUser(): User {
        return User("zhangsan", 20, 180.1F, arrayListOf())
    }

    private fun startAutoLog() {
        autoHandler.postDelayed(autoRunnable, 100)
    }

    private fun stopAutoLog() {
        autoHandler.removeCallbacks(autoRunnable)
    }

    override fun onDestroy() {
        cancel()
        stopAutoLog()
        super.onDestroy()
    }
}
