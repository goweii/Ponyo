package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.AppStack
import per.goweii.ponyo.crash.Crash
import per.goweii.ponyo.panel.actistack.ActiStackManager
import per.goweii.ponyo.panel.log.Manager
import per.goweii.ponyo.panel.tm.TM
import per.goweii.ponyo.panel.tm.TmManager
import per.goweii.ponyo.timemonitor.TimeMonitor

object Ponyo : AppStack.AppLifecycleListener {

    private lateinit var floatManager: FloatManager

    fun initialize(application: Application) {
        if (this::floatManager.isInitialized) return
        Manager.start()
        TM.APP_STARTUP.start("application initialize")
        TimeMonitor.registerTimeLineEndListener(TmManager)
        Crash.setCrashActivity(CrashActivity::class.java)
        AppStack.registerAppLifecycleListener(Ponyo)
        AppStack.activityStack.registerActivityLifecycleListener(TmManager)
        AppStack.activityStack.registerStackUpdateListener(ActiStackManager)
        floatManager = FloatManager(application).icon(R.drawable.ponyo_ic_float)
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

    internal fun onLoggerAssert(count: Int) {
        floatManager.setLogAssertCount(count)
    }

    internal fun onLoggerError(count: Int) {
        floatManager.setLogErrorCount(count)
    }

    internal fun onLoggerWarn(count: Int) {
        floatManager.setLogWarnCount(count)
    }
}