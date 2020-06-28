package per.goweii.ponyo.startup.annotation

class Const {
    companion object {
        private const val ROOT_PACKAGE_NAME = "per.goweii.ponyo.startup"

        const val INITIALIZER_CLASS_NAME = "$ROOT_PACKAGE_NAME.Initializer"
        const val GENERATED_PACKAGE_NAME = "$ROOT_PACKAGE_NAME.holder"
        const val GENERATED_INIT_META_FIELD = "initMeta"
        const val INIT_HOLDER_PACKAGE_NAME = "$ROOT_PACKAGE_NAME.annotation"
        const val INIT_HOLDER_SIMPLE_CLASS_NAME = "InitHolder"
        const val INIT_HOLDER_GET_METHOD_NAME = "getInitMeta"
    }
}