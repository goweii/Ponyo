package per.goweii.ponyo.startup

internal class InitRunner {

    private val list: ArrayList<String> = arrayListOf()

    fun add(initName: String): InitRunner {
        list.add(initName)
        return this
    }

    fun run() {
        val syncTasks: ArrayList<Initializer> = arrayListOf()
        val asyncTasks: ArrayList<Initializer> = arrayListOf()
        for (initName in list) {
            val initializer = Starter.getOrCreateInitializer(initName)
            if (initializer.async()) {
                asyncTasks.add(initializer)
            } else {
                syncTasks.add(initializer)
            }
        }
        if (syncTasks.isNotEmpty()) {
            runSync(syncTasks)
        }
        if (asyncTasks.isNotEmpty()) {
            runAsync(asyncTasks)
        }
    }

    private fun runSync(tasks: ArrayList<Initializer>) {
        val runner = SyncRunner(tasks)
        runner.execute()
    }

    private fun runAsync(tasks: ArrayList<Initializer>) {
        val runner = SyncRunner(tasks)
        AsyncRunner(runner).execute()
    }
}