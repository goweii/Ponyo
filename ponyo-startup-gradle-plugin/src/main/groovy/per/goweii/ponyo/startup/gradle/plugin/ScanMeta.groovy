package per.goweii.ponyo.startup.gradle.plugin

class ScanMeta {
    String className = null
    Boolean async = false
    int priority = 0
    List<String> activities = new ArrayList<>()
    List<String> fragments = new ArrayList<>()
}