package per.goweii.android.ponyo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import per.goweii.ponyo.log.Ponlog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log()
    }

    private fun log() {
        Ponlog.v("MainActivityTag") { "ponlog print msg" }
    }
}
