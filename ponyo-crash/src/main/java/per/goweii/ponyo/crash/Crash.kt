package per.goweii.ponyo.crash

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

object Crash {
    fun initialize(application: Application) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        if (defaultHandler is CrashHandler) return
        val crashHandler = CrashHandler(application, defaultHandler)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    crashHandler.uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
    }

    fun setCrashActivity(cls: Class<out Activity>) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        if (defaultHandler !is CrashHandler) return
        defaultHandler.customCrashActivity = cls
    }

    fun restartApp(context: Context) {
        val application = context.applicationContext
        val pi = try {
            application.packageManager.getPackageInfo(application.packageName, 0)
        } catch (e: Exception) {
            return
        }
        val resolveIntent = Intent(Intent.ACTION_MAIN, null)
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveIntent.setPackage(pi.packageName)
        val resolves = application.packageManager.queryIntentActivities(resolveIntent, 0)
        val resolve0 = resolves.iterator().next()
        if (resolve0 != null) {
            val packageName = resolve0.activityInfo.packageName
            val className = resolve0.activityInfo.name
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.component = ComponentName(packageName, className)
            application.startActivity(intent)
        }
    }
}