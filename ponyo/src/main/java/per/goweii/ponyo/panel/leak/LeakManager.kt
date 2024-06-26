package per.goweii.ponyo.panel.leak

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import per.goweii.ponyo.R
import per.goweii.ponyo.leak.Leak

object LeakManager : Leak.LeakListener, Leak.AnalyzeListener {

    private lateinit var ponyo_ll_loading: LinearLayout
    private lateinit var ponyo_pb_loading: ProgressBar
    private lateinit var ponyo_tv_loading: TextView
    private lateinit var ponyo_tv_start: TextView
    private lateinit var ponyo_hsv: ViewGroup
    private lateinit var tv_leak: TextView

    fun attach(view: View) {
        ponyo_ll_loading = view.findViewById(R.id.ponyo_ll_loading)
        ponyo_pb_loading = view.findViewById(R.id.ponyo_pb_loading)
        ponyo_tv_loading = view.findViewById(R.id.ponyo_tv_loading)
        ponyo_tv_start = view.findViewById(R.id.ponyo_tv_start)
        ponyo_hsv = view.findViewById(R.id.ponyo_hsv)
        tv_leak = view.findViewById(R.id.tv_leak)

        ponyo_hsv.visibility = View.GONE
        ponyo_tv_start.visibility = View.VISIBLE
        ponyo_ll_loading.visibility = View.GONE

        Leak.setLeakListener(this)
        Leak.setAnalyzeListener(this)
        ponyo_tv_start.setOnClickListener {
            Leak.dumpAndAnalyze()
        }
    }

    override fun onLeak(count: Int) {
        ponyo_tv_start.isEnabled = true
    }

    override fun onProgress(percent: Float, desc: String) {
        ponyo_hsv.visibility = View.GONE
        ponyo_tv_start.isEnabled = false
        ponyo_ll_loading.visibility = View.VISIBLE
        val indeterminate = percent < 0F
        val max = 10000
        val progress = when {
            percent < 0 -> 0
            percent > 1 -> 1
            else -> (max * percent).toInt()
        }
        ponyo_pb_loading.isIndeterminate = indeterminate
        ponyo_pb_loading.max = max
        ponyo_pb_loading.progress = progress
        ponyo_tv_loading.text = desc
    }

    override fun onAnalysis(heapAnalysis: String) {
        ponyo_ll_loading.visibility = View.GONE
        ponyo_hsv.visibility = View.VISIBLE
        ponyo_tv_start.isEnabled = false
        tv_leak.text = heapAnalysis
    }
}