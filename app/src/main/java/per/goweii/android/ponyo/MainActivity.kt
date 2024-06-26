package per.goweii.android.ponyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import per.goweii.android.ponyo.appstack.ActiStackActivity
import per.goweii.android.ponyo.leak.LeakActivity
import per.goweii.android.ponyo.log.LogActivity
import per.goweii.android.ponyo.net.NetActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_print_log.setOnClickListener {
            startActivity(Intent(this@MainActivity, LogActivity::class.java))
        }
        tv_net.setOnClickListener {
            startActivity(Intent(this@MainActivity, NetActivity::class.java))
        }
        tv_activity_stack.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActiStackActivity::class.java))
        }
        tv_leak.setOnClickListener {
            startActivity(Intent(this@MainActivity, LeakActivity::class.java))
        }
    }
}
