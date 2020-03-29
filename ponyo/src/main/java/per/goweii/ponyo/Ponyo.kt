package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.OnAppStateChangeListener

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
    }

    override fun onResume() {
    }

    override fun onPause() {
        floatManager?.collapse()
    }

    override fun onStop() {
    }
}