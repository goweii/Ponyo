package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.ActivityStack
import per.goweii.ponyo.appstack.AppStack
import per.goweii.ponyo.crash.Crash
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.panel.actistack.ActiStackManager
import per.goweii.ponyo.panel.log.GsonFormatter
import per.goweii.ponyo.panel.log.LogManager
import per.goweii.ponyo.panel.tm.TM
import per.goweii.ponyo.panel.tm.TmManager
import per.goweii.ponyo.timemonitor.TimeMonitor

object Ponyo : AppStack.AppLifecycleListener {

    private lateinit var floatManager: FloatManager

    fun initialize(application: Application) {
        if (this::floatManager.isInitialized) return
        TM.APP_STARTUP.start("application initialize")
        Crash.initialize(application)
        Crash.setCrashActivity(CrashActivity::class.java)
        AppStack.initialize(application)
        AppStack.registerAppLifecycleListener(Ponyo)
        Ponlog.addLogPrinter(LogManager)
        Ponlog.setJsonFormatter(GsonFormatter())
        TimeMonitor.registerTimeLineEndListener(TmManager)
        ActivityStack.registerActivityLifecycleListener(TmManager)
        ActivityStack.registerStackUpdateListener(ActiStackManager)
        floatManager = FloatManager(application).icon(R.drawable.ponyo)
    }

    override fun onCreate() {
        TM.APP_STARTUP.record("application created")
    }

    override fun onStart() {
        TM.APP_STARTUP.record("application started")
    }

    override fun onResume() {
        TM.APP_STARTUP.record("application resumed")
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