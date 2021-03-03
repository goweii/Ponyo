package per.goweii.ponyo.log.utils

import android.app.ActivityManager
import android.content.Context

object PidUtils {
    fun getAllPid(context: Context): Map<String, Int> {
        val pids: MutableMap<String, Int> = HashMap()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses
        for (runningAppProcess in runningAppProcesses) {
            val processName = runningAppProcess.processName
            val pid = runningAppProcess.pid
            pids[processName] = pid
        }
        return pids
    }
}