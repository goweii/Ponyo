package per.goweii.ponyo.appstack

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

object AppStack : LifecycleObserver {

    lateinit var activityStack: ActivityStack

    private var appLifecycleListeners = arrayListOf<AppLifecycleListener>()

    fun initialize(application: Application) {
        if (this::activityStack.isInitialized) return
        activityStack = ActivityStack.create(application)
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycle)
    }

    fun registerAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.add(listener)
    }

    fun unregisterAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.remove(listener)
    }

    private val applicationLifecycle = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            appLifecycleListeners.forEach { it.onCreate() }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            appLifecycleListeners.forEach { it.onStart() }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            appLifecycleListeners.forEach { it.onResume() }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            appLifecycleListeners.forEach { it.onPause() }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            appLifecycleListeners.forEach { it.onStop() }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            appLifecycleListeners.forEach { it.onDestroy() }
        }
    }

    interface AppLifecycleListener {
        fun onCreate()
        fun onStart()
        fun onResume()
        fun onPause()
        fun onStop()
        fun onDestroy()
    }
}

