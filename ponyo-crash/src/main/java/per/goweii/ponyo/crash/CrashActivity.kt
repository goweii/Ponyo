package per.goweii.ponyo.crash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.ponyo_crash_activity_crash.*
import java.io.PrintWriter
import java.io.StringWriter

class CrashActivity: AppCompatActivity() {

    companion object {
        private const val PARAM_ERROR = "PARAM_ERROR"

        fun start(context: Context, e: Throwable) {
            context.startActivity(Intent(context, CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PARAM_ERROR, e)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ponyo_crash_activity_crash)
        ponyo_crash_btn_exit.setOnClickListener {
            finish()
            Process.killProcess(Process.myPid())
            System.exit(10)
        }
        val e = intent.getSerializableExtra(PARAM_ERROR) as Throwable
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        ponyo_crash_tv_error.text = stringWriter.toString()
    }
}