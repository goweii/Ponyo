package per.goweii.android.ponyo.multiprocess

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

open class BaseLogService: Service() {
    private val handler = Handler()
    private var count = 0

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(this::class.java.simpleName, "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(this::class.java.simpleName, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(this::class.java.simpleName, "onStartCommand")
        startLog()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        Log.d(this::class.java.simpleName, "onDestroy")
    }

    private fun startLog() {
        handler.postDelayed({
            count++
            Log.d(this::class.java.simpleName, count.toString())
            startLog()
        }, 200)
    }
}