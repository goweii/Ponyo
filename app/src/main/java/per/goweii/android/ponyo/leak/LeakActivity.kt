package per.goweii.android.ponyo.leak

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_leak.*
import per.goweii.android.ponyo.R

class LeakActivity: AppCompatActivity() {

    companion object {
        val leakContexts = arrayListOf<Context>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak)
        btn_clear.setOnClickListener {
            LeakFragmentHolder.leakFragments.clear()
            LeakFragment.leakFragments.clear()
            leakContexts.clear()
        }
        supportFragmentManager.apply {
            beginTransaction().apply {
                replace(
                    R.id.fl_container,
                    LeakFragment()
                )
            }.commit()
        }
        leakContexts.add(this@LeakActivity)
    }
}