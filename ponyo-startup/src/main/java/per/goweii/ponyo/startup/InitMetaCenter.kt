package per.goweii.ponyo.startup

import per.goweii.ponyo.startup.annotation.InitMeta

class InitMetaCenter {
    fun loadInitMeta(initMetas: Map<String, InitMeta>){
        throw RuntimeException("load InitMeta error")
    }
}