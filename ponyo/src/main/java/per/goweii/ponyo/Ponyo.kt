package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.appstack.AppStack
import per.goweii.ponyo.crash.Crash
import per.goweii.ponyo.dialog.FrameDialog
import per.goweii.ponyo.log.Logcat
import per.goweii.ponyo.panel.Panel
import per.goweii.ponyo.panel.PanelManager
import per.goweii.ponyo.panel.actistack.ActiStackManager
import per.goweii.ponyo.panel.log.LogManager
import per.goweii.ponyo.panel.tm.TM
import per.goweii.ponyo.panel.tm.TmManager
import per.goweii.ponyo.timemonitor.TimeMonitor

object Ponyo : AppStack.AppLifecycleListener {
    lateinit var application: Application private set
    private var floatWindow: FloatWindow? = null

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        Logcat.registerCatchListener(LogManager)
        Logcat.start()
        TM.APP_STARTUP.start("Application onInitialize")
        TimeMonitor.registerTimeLineEndListener(TmManager)
        Crash.setCrashActivity(CrashActivity::class.java)
        AppStack.registerAppLifecycleListener(Ponyo)
        AppStack.activityStack.registerActivityLifecycleListener(TmManager)
        AppStack.activityStack.registerStackUpdateListener(ActiStackManager)
    }

    fun addPanel(panel: Panel) {
        PanelManager.addPanel(panel)
    }

    override fun onCreate() {
        TM.APP_STARTUP.record("Application onCreate")
        floatWindow = FloatWindow(Ponyo.application)
    }

    override fun onStart() {
        TM.APP_STARTUP.record("Application onStart")
    }

    override fun onResume() {
        TM.APP_STARTUP.record("Application onResume")
        if (OverlayUtils.canOverlay(application)) {
            floatWindow?.show()
        } else {
            OverlayUtils.requestOverlayPermission(application)
        }
    }

    override fun onPause() {
        floatWindow?.dismiss()
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    internal fun onLoggerAssert(count: Int) {
        floatWindow?.setLogAssertCount(count)
    }

    internal fun onLoggerError(count: Int) {
        floatWindow?.setLogErrorCount(count)
    }
}