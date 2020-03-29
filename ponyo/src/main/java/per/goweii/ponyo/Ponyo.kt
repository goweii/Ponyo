package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.OnAppStateChangeListener
import per.goweii.ponyo.log.Ponlog

object Ponyo : OnAppStateChangeListener {

    private lateinit var application: Application
    private var floatManager: FloatManager? = null

    fun attach(app: Application) = apply {
        application = app
        ActivityStack.registerOnAppStateChangeListener(this)
    }

    fun showFloat(iconResId: Int) {
        floatManager = FloatManager(application).icon(iconResId)
        floatManager?.show()
    }

    override fun onStart() {
        Ponlog.d { "onStart" }
    }

    override fun onResume() {
        Ponlog.d { "onResume" }
    }

    override fun onPause() {
        Ponlog.d { "onPause" }
        floatManager?.collapse()
    }

    override fun onStop() {
        Ponlog.d { "onStop" }
    }
}