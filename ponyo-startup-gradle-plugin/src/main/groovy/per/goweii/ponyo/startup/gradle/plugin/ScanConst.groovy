package per.goweii.ponyo.startup.gradle.plugin

class ScanConst {
    static final String PLUGIN_NAME = "per.goweii.ponyo.startup"

    static final String GENERATE_TO_CLASS_NAME = 'per/goweii/ponyo/startup/InitMetaCenter'
    static final String GENERATE_TO_CLASS_FILE_NAME = GENERATE_TO_CLASS_NAME + '.class'
    static final String GENERATE_TO_METHOD_NAME = 'loadInitMeta'

    static final String ANNOTATION_PACKAGE_NAME = 'per/goweii/ponyo/startup/annotation/'
    static final String ANNOTATION_DESC = 'L' + ANNOTATION_PACKAGE_NAME + 'Startup;'

    static final String INTERFACE_PACKAGE_NAME = 'per/goweii/ponyo/startup/'
    static final String INTERFACE_CLASS_NAME = INTERFACE_PACKAGE_NAME + 'Initializer'

    static final String REGISTER_METHOD_NAME = 'register'
}