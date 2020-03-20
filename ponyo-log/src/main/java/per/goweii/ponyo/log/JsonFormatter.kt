package per.goweii.ponyo.log

interface JsonFormatter {
    fun toJson(any: Any) : String
}