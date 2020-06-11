package per.goweii.ponyo

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.ponyo_activity_crash.*
import per.goweii.ponyo.crash.Crash
import java.io.PrintWriter
import java.io.StringWriter

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.ponyo_activity_crash)
        ponyo_crash_tv_copy_log.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("error", ponyo_crash_tv_error.text))
            ponyo_crash_tv_copy_log.text = "已复制"
        }
        ponyo_crash_btn_exit.setOnClickListener {
            finish()
            Process.killProcess(Process.myPid())
            System.exit(10)
        }
        ponyo_crash_btn_restart.setOnClickListener {
            Crash.restartApp(applicationContext)
            finish()
            Process.killProcess(Process.myPid())
        }
        showLog()
    }

    private fun showLog() {
        val e = intent.getSerializableExtra("error") as Throwable
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        ponyo_crash_tv_error.text = stringWriter.toString().toDBC()
    }

    private fun String.toDBC(): String {
        val c = this.toCharArray()
        for (i in c.indices) {
            if (c[i].toInt() == 12288) {
                c[i] = 32.toChar()
                continue
            }
            if (c[i].toInt() in 65281..65374) c[i] = (c[i] - 65248)
        }
        return String(c)
    }
}