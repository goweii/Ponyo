package per.goweii.ponyo.appstack

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import per.goweii.ponyo.log.Ponlog

object AppLifecycle : LifecycleObserver {

    lateinit var application: Application
        internal set

    private var appLifecycleListeners = arrayListOf<AppLifecycleListener>()

    fun registerAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.add(listener)
    }

    fun unregisterAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.remove(listener)
    }

    fun onInitialize(application: Application) {
        this.application = application
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycle)
        this.application.registerActivityLifecycleCallbacks(ActivityStack)
    }

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

    interface AppLifecycleListener {
        fun onCreate()
        fun onStart()
        fun onResume()
        fun onPause()
        fun onStop()
        fun onDestroy()
    }
}

