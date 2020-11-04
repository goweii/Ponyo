package per.goweii.ponyo.startup.gradle.plugin

class ScanMeta {
    String interfaceName = ''
    ArrayList<String> classList = new ArrayList<>()

    ScanMeta(String interfaceName){
        this.interfaceName = ScanConst.INTERFACE_PACKAGE_NAME + interfaceName
    }
}