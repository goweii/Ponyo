package per.goweii.ponyo.log

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

object LogTimeUtils {
    private val sdf = object : ThreadLocal<SimpleDateFormat>() {
        @SuppressLint("SimpleDateFormat")
        override fun initialValue(): SimpleDateFormat? {
            return SimpleDateFormat("MM-dd HH:mm:ss.SSS")
        }
    }

    /**
     * 03-05 13:48:27.774
     */
    fun plus1ms(timestamp: String): String {
        val sdf = sdf.get()!!
        val data = sdf.parse(timestamp)
        data.time = data.time + 1
        return sdf.format(data)
    }
}