package per.goweii.ponyo.panel.tm

import per.goweii.ponyo.timemonitor.TimeMonitor

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
enum class TM {
    APP_STARTUP;

    fun start(tag: String){
        TimeMonitor.start(name, tag)
    }

    fun record(tag: String){
        TimeMonitor.record(name, tag)
    }

    fun end(tag: String){
        TimeMonitor.end(name, tag)
    }
}