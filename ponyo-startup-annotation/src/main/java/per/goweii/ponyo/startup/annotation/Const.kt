package per.goweii.ponyo.startup.annotation

class Const {
    companion object {
        private const val ROOT_PACKAGE_NAME = "per.goweii.ponyo.startup"

        const val INITIALIZER_CLASS_NAME = "$ROOT_PACKAGE_NAME.Initializer"
        const val GENERATED_PACKAGE_NAME = "$ROOT_PACKAGE_NAME.holder"
        const val GENERATED_CLASS_NAME = "StartupHolder"
        const val GENERATED_LIST_FIELD = "initializers"
    }
}