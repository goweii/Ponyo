package per.goweii.ponyo.panel.log

import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.Ponlog

data class LogEntity(
    val level: Ponlog.Level,
    val tag: String,
    val body: LogBody,
    val msg: String
)