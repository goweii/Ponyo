package per.goweii.ponyo.startup

import android.app.Application

internal class InitializerRunner(private val application: Application) {

    private val initializers: ArrayList<Initializer> = arrayListOf()

    private val isMainProcess = Startup.isMainProcess()

    fun add(task: Initializer): InitializerRunner {
        initializers.add(task)
        return this
    }

    fun run() {
        val syncTasks: ArrayList<Initializer> = arrayListOf()
        val asyncTasks: ArrayList<Initializer> = arrayListOf()
        for (initializer in initializers) {
            if (initializer.async()) {
                asyncTasks.add(initializer)
            } else {
                syncTasks.add(initializer)
            }
        }
        runSync(syncTasks)
        runAsync(asyncTasks)
    }

    private fun runSync(tasks: ArrayList<Initializer>) {
        tasks.sortBy { it.priority() }
        for (task in tasks) {
            try {
                task.initialize(application, isMainProcess)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun runAsync(tasks: ArrayList<Initializer>) {
        AsyncRunner(application, isMainProcess, tasks).execute()
    }
}