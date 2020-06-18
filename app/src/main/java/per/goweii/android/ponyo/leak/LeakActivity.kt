package per.goweii.android.ponyo.leak

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_leak.*
import per.goweii.android.ponyo.R
import per.goweii.ponyo.leak.Leak

class LeakActivity: AppCompatActivity() {

    companion object {
        private var context: Context? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak)
        btn_leak.setOnClickListener {
            context = this@LeakActivity
        }
        btn_remove.setOnClickListener {
            context = null
        }
        btn_dump.setOnClickListener {
            Leak.dump()
        }
    }
}