package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.AppLifecycle

object Ponyo : AppLifecycle.AppLifecycleListener {

    private lateinit var application: Application
    private var floatManager: FloatManager? = null

    fun attach(app: Application) = apply {
        application = app
        AppLifecycle.registerAppLifecycleListener(this)
    }

    fun showFloat(iconResId: Int) {
        floatManager = FloatManager(application).icon(iconResId)
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
        floatManager?.show()
    }

    override fun onPause() {
        floatManager?.dismiss()
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}