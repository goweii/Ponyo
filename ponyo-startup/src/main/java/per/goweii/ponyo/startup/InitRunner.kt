package per.goweii.ponyo.startup

import per.goweii.ponyo.startup.annotation.InitMeta

internal class InitRunner {

    private val initMetas: ArrayList<InitMeta> = arrayListOf()

    fun add(initMeta: InitMeta): InitRunner {
        initMetas.add(initMeta)
        return this
    }

    fun run() {
        val syncTasks: ArrayList<InitMeta> = arrayListOf()
        val asyncTasks: ArrayList<InitMeta> = arrayListOf()
        for (initMeta in initMetas) {
            if (initMeta.async) {
                asyncTasks.add(initMeta)
            } else {
                syncTasks.add(initMeta)
            }
        }
        if (syncTasks.isNotEmpty()) {
            runSync(syncTasks)
        }
        if (asyncTasks.isNotEmpty()) {
            runAsync(asyncTasks)
        }
    }

    private fun runSync(tasks: ArrayList<InitMeta>) {
        val runner = SyncRunner(tasks)
        runner.execute()
    }

    private fun runAsync(tasks: ArrayList<InitMeta>) {
        val runner = SyncRunner(tasks)
        AsyncRunner(runner).execute()
    }
}