package per.goweii.ponyo.log

import java.io.IOException
import java.util.*

class LogCommand {
    private val args: MutableList<String> = ArrayList()

    init {
        args.add("logcat")
        args.add("-v")
        args.add("time")
    }

    fun addBuffer(buffer: Buffer) {
        args.add("-b")
        args.add(buffer.value)
    }

    fun setPid(pid: Int) {
        args.add("--pid")
        args.add(pid.toString())
    }

    fun setSinceTime(sinceTime: String) {
        args.add("-T")
        args.add(sinceTime)
    }

    @Throws(IOException::class)
    fun exec(): Process {
        return Runtime.getRuntime().exec(args.toTypedArray())
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in args.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            sb.append(args[i])
        }
        return sb.toString()
    }

    enum class Buffer(val value: String) {
        MAIN("main"),
        SYSTEM("system"),
        EVENTS("events"),
        RADIO("radio"),
        ;
    }
}