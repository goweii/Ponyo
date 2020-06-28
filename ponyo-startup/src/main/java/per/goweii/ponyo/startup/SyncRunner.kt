package per.goweii.ponyo.startup

internal class SyncRunner(
    private val initializers: ArrayList<Initializer>
) {
    fun execute() {
        initializers.sortBy {
            it.priority()
        }
        initializers.forEach {
            Starter.initialize(it)
        }
    }
}