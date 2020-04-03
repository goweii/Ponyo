package per.goweii.ponyo.panel.tm

import android.text.TextUtils
import per.goweii.ponyo.timemonitor.TimeLineEndListener

object TmManager : TimeLineEndListener {

    private var tmAdapter: TmAdapter? = null
    private val datas = arrayListOf<TmEntity>()

    fun attach(tmAdapter: TmAdapter) {
        this.tmAdapter = tmAdapter
        tmAdapter.set(datas)
    }

    override fun onEnd(lineTag: String, lineInfo: String) {
        var item: TmEntity? = null
        datas.forEach {
            if (TextUtils.equals(it.lineTag, lineTag)) {
                item = it
                return@forEach
            }
        }
        item?.let {
            it.lineInfo = lineInfo
        } ?: let {
            val tmEntity = TmEntity(lineTag, lineInfo)
            datas.add(tmEntity)
        }
        tmAdapter?.set(datas)
    }

}