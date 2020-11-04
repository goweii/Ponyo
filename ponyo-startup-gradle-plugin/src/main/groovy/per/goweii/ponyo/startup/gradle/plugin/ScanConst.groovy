package per.goweii.ponyo.startup.gradle.plugin

class ScanConst {
    static final String PLUGIN_NAME = "per.goweii.ponyo.startup"

    static final String GENERATE_TO_CLASS_NAME = 'per/goweii/ponyo/startup/StartupCenter'
    static final String GENERATE_TO_CLASS_FILE_NAME = GENERATE_TO_CLASS_NAME + '.class'
    static final String GENERATE_TO_METHOD_NAME = 'getInitMeta'

    static final String ROUTER_CLASS_PACKAGE_NAME = 'per/goweii/ponyo/startup/annotation/'
    static final String INTERFACE_PACKAGE_NAME = 'per/goweii/ponyo/startup/provider/'
    static final String REGISTER_METHOD_NAME = 'register'
}