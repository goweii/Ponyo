package per.goweii.ponyo.appstack

import android.app.Activity
import androidx.annotation.NonNull

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
interface ActivityLifecycleListener {
    fun onCreated(@NonNull activity: Activity)
    fun onStarted(@NonNull activity: Activity)
    fun onResumed(@NonNull activity: Activity)
    fun onPaused(@NonNull activity: Activity)
    fun onStopped(@NonNull activity: Activity)
    fun onDestroyed(@NonNull activity: Activity)
}