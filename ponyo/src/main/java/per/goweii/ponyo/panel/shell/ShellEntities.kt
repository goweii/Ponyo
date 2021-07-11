package per.goweii.ponyo.panel.shell

abstract class ShellEntity {
    abstract val itemType: Int
}

data class ShellInputEntity(
    val input: String
) : ShellEntity() {
    override val itemType: Int = ITEM_TYPE

    companion object {
        const val ITEM_TYPE = 1
    }
}

data class ShellOutputEntity(
    val output: StringBuilder
) : ShellEntity() {
    override val itemType: Int = ITEM_TYPE

    fun append(text: String) {
        output.append("\n").append(text)
    }

    companion object {
        const val ITEM_TYPE = 2
    }
}