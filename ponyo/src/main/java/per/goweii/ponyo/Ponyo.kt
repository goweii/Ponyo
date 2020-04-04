package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.AppLifecycle
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.panel.actistack.ActiStackManager
import per.goweii.ponyo.panel.log.LogManager
import per.goweii.ponyo.panel.tm.TmManager
import per.goweii.ponyo.timemonitor.TimeMonitor

object Ponyo : AppLifecycle.AppLifecycleListener {

    private lateinit var floatManager: FloatManager

    fun onInitialize(application: Application) {
        AppLifecycle.registerAppLifecycleListener(Ponyo)
        Ponlog.addLogPrinter(LogManager)
        Ponlog.setJsonFormatter(GsonFormatter())
        TimeMonitor.registerTimeLineEndListener(TmManager)
        ActivityStack.registerStackUpdateListener(ActiStackManager)
        floatManager = FloatManager(application).icon(R.drawable.ponyo)
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
        floatManager.show()
    }

    override fun onPause() {
        floatManager.dismiss()
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}