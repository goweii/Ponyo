package per.goweii.ponyo.startup

import per.goweii.ponyo.startup.annotation.InitMeta

internal class SyncRunner(
    private val initMetas: ArrayList<InitMeta>
) {
    fun execute() {
        initMetas.sortBy {
            it.priority
        }
        initMetas.forEach {
            Starter.initialize(it.className)
        }
    }
}