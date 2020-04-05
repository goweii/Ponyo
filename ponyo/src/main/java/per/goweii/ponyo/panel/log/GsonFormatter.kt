package per.goweii.ponyo.panel.log

import com.google.gson.GsonBuilder
import per.goweii.ponyo.log.JsonFormatter

class GsonFormatter : JsonFormatter {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun toJson(any: Any): String {
        return gson.toJson(any)
    }
}