package per.goweii.ponyo.log

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.JsonWriter
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*

internal object LogFormatter {

    var jsonFormatter: JsonFormatter? = null

    fun object2String(obj: Any?): String {
        obj ?: return "null"
        return when (obj) {
            is CharSequence -> obj.toString()
            obj.javaClass.isArray -> array2String(obj)
            is Throwable -> throwable2String(obj)
            is Bundle -> bundle2String(obj)
            is Intent -> intent2String(obj)
            else -> {
                jsonFormatter?.toJson(obj) ?: run {
                    obj.toString()
                }
            }
        }
    }

    private fun array2String(obj: Any): String {
        return when (obj) {
            is BooleanArray -> obj.contentToString()
            is ByteArray -> obj.contentToString()
            is CharArray -> obj.contentToString()
            is DoubleArray -> obj.contentToString()
            is FloatArray -> obj.contentToString()
            is IntArray -> obj.contentToString()
            is LongArray -> obj.contentToString()
            is ShortArray -> obj.contentToString()
            is Array<*> -> obj.contentDeepToString()
            else -> obj.toString()
        }
    }

    private fun throwable2String(e: Throwable): String {
        return getFullStackTrace(e)
    }

    private fun bundle2String(bundle: Bundle): String {
        val iterator = bundle.keySet().iterator()
        if (!iterator.hasNext()) {
            return "Bundle {}"
        }
        val sb = StringBuilder(128)
        sb.append("Bundle { ")
        while (true) {
            val key = iterator.next()
            val value = bundle.get(key)
            sb.append(key).append('=')
            if (value is Bundle) {
                sb.append(if (value === bundle) "(this Bundle)" else bundle2String((value as Bundle?)!!))
            } else {
                sb.append(object2String(value))
            }
            if (!iterator.hasNext()) return sb.append(" }").toString()
            sb.append(',').append(' ')
        }
    }

    private fun intent2String(intent: Intent): String {
        val sb = StringBuilder(128)
        sb.append("Intent { ")
        var first = true
        val mAction = intent.action
        if (mAction != null) {
            sb.append("act=").append(mAction)
            first = false
        }
        val mCategories = intent.categories
        if (mCategories != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cat=[")
            var firstCategory = true
            for (c in mCategories) {
                if (!firstCategory) {
                    sb.append(',')
                }
                sb.append(c)
                firstCategory = false
            }
            sb.append("]")
        }
        val mData = intent.data
        if (mData != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("dat=").append(mData)
        }
        val mType = intent.type
        if (mType != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("typ=").append(mType)
        }
        val mFlags = intent.flags
        if (mFlags != 0) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("flg=0x").append(Integer.toHexString(mFlags))
        }
        val mPackage = intent.getPackage()
        if (mPackage != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("pkg=").append(mPackage)
        }
        val mComponent = intent.component
        if (mComponent != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cmp=").append(mComponent.flattenToShortString())
        }
        val mSourceBounds = intent.sourceBounds
        if (mSourceBounds != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("bnds=").append(mSourceBounds.toShortString())
        }
        val mClipData = intent.clipData
        if (mClipData != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            clipData2String(mClipData, sb)
        }
        val mExtras = intent.extras
        if (mExtras != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("extras={")
            sb.append(bundle2String(mExtras))
            sb.append('}')
        }
        val mSelector = intent.selector
        if (mSelector != null) {
            if (!first) {
                sb.append(' ')
            }
            sb.append("sel={")
            sb.append(if (mSelector === intent) "(this Intent)" else intent2String(mSelector))
            sb.append("}")
        }
        sb.append(" }")
        return sb.toString()
    }

    private fun clipData2String(clipData: ClipData, sb: StringBuilder) {
        val item = clipData.getItemAt(0)
        if (item == null) {
            sb.append("ClipData.Item {}")
            return
        }
        sb.append("ClipData.Item { ")
        val mHtmlText = item.htmlText
        if (mHtmlText != null) {
            sb.append("H:")
            sb.append(mHtmlText)
            sb.append("}")
            return
        }
        val mText = item.text
        if (mText != null) {
            sb.append("T:")
            sb.append(mText)
            sb.append("}")
            return
        }
        val uri = item.uri
        if (uri != null) {
            sb.append("U:").append(uri)
            sb.append("}")
            return
        }
        val intent = item.intent
        if (intent != null) {
            sb.append("I:")
            sb.append(intent2String(intent))
            sb.append("}")
            return
        }
        sb.append("NULL")
        sb.append("}")
    }

    private fun getFullStackTrace(t: Throwable?): String {
        fun getStackFrameList(throwable: Throwable): List<String> {
            val sw = StringWriter()
            val pw = PrintWriter(sw, true)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()
            val frames = StringTokenizer(stackTrace, System.lineSeparator())
            val list = ArrayList<String>()
            var traceStarted = false
            while (frames.hasMoreTokens()) {
                val token = frames.nextToken()
                val at = token.indexOf("at")
                if (at != -1 && token.substring(0, at).trim { it <= ' ' }.isEmpty()) {
                    traceStarted = true
                    list.add(token)
                } else if (traceStarted) {
                    break
                }
            }
            return list
        }

        fun removeCommonFrames(causeFrames: List<String>, wrapperFrames: List<String>) {
            var causeFrameIndex = causeFrames.size - 1
            var wrapperFrameIndex = wrapperFrames.size - 1
            while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
                val causeFrame = causeFrames[causeFrameIndex]
                val wrapperFrame = wrapperFrames[wrapperFrameIndex]
                if (causeFrame == wrapperFrame) {
                    causeFrames.toMutableList().removeAt(causeFrameIndex)
                }
                causeFrameIndex--
                wrapperFrameIndex--
            }
        }

        var throwable = t
        val throwableList = ArrayList<Throwable>()
        while (throwable != null && !throwableList.contains(throwable)) {
            throwableList.add(throwable)
            throwable = throwable.cause
        }
        val size = throwableList.size
        val frames = ArrayList<String>()
        var nextTrace = getStackFrameList(throwableList[size - 1])
        var i = size
        while (--i >= 0) {
            val trace = nextTrace
            if (i != 0) {
                nextTrace = getStackFrameList(throwableList[i - 1])
                removeCommonFrames(trace, nextTrace)
            }
            if (i == size - 1) {
                frames.add(throwableList[i].toString())
            } else {
                frames.add(" Caused by: " + throwableList[i].toString())
            }
            frames.addAll(trace)
        }
        val sb = StringBuilder()
        for (element in frames) {
            sb.append(element).append(System.lineSeparator())
        }
        return sb.toString()
    }
}