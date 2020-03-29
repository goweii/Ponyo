package per.goweii.ponyo.log

interface LogPrinter {
    fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String)
}