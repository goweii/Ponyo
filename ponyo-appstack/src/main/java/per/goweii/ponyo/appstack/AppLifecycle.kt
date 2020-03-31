package per.goweii.ponyo.appstack

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import per.goweii.ponyo.log.Ponlog

object AppLifecycle: LifecycleObserver {
    private var appLifecycleListeners = arrayListOf<AppLifecycleListener>()

    fun registerAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.add(listener)
    }

    fun unregisterAppLifecycleListener(listener: AppLifecycleListener) {
        appLifecycleListeners.remove(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        Ponlog.d { "onCreate" }
        appLifecycleListeners.forEach { it.onCreate() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        Ponlog.d { "onStart" }
        appLifecycleListeners.forEach { it.onStart() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        Ponlog.d { "onResume" }
        appLifecycleListeners.forEach { it.onResume() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        Ponlog.d { "onPause" }
        appLifecycleListeners.forEach { it.onPause() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        Ponlog.d { "onStop" }
        appLifecycleListeners.forEach { it.onStop() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        Ponlog.d { "onDestroy" }
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

