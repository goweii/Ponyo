package per.goweii.ponyo.panel.tm

import android.text.TextUtils

object TmManager : (String, String) -> Unit {

    private var tmAdapter: TmAdapter? = null
    private val datas = arrayListOf<TmEntity>()

    override fun invoke(lineTag: String, lineInfo: String) {
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

    fun attach(tmAdapter: TmAdapter) {
        this.tmAdapter = tmAdapter
        tmAdapter.set(datas)
    }

}