package per.goweii.ponyo

import android.app.Application
import com.google.gson.GsonBuilder
import per.goweii.ponyo.appstack.AppLifecycle
import per.goweii.ponyo.log.JsonFormatter
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.panel.db.DbManager
import per.goweii.ponyo.panel.log.LogManager

object Ponyo : AppLifecycle.AppLifecycleListener {

    private lateinit var application: Application

    private val gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }
    private var floatManager: FloatManager? = null

    fun attach(app: Application) = apply {
        application = app
        AppLifecycle.registerAppLifecycleListener(this)
        Ponlog.addLogPrinter(LogManager)
        Ponlog.setJsonFormatter(object : JsonFormatter {
            override fun toJson(any: Any): String {
                return gson.toJson(any)
            }
        })
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