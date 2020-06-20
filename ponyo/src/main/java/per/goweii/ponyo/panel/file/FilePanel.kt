package per.goweii.ponyo.panel.file

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class FilePanel : BasePanel() {
    private val fileTabAdapter by lazy {
        FileTabAdapter { onTabClick(it) }
    }
    private val fileNaviAdapter by lazy {
        FileNaviAdapter { onNaviClick(it) }
    }
    private val fileNameAdapter by lazy {
        FileNameAdapter { onNameClick(it) }
    }
    private lateinit var rv_file_tab: RecyclerView
    private lateinit var rv_file_navi: RecyclerView
    private lateinit var tv_navi_length: TextView
    private lateinit var rv_file_name: RecyclerView
    private lateinit var ll_file_str: LinearLayout
    private lateinit var tv_file_str_name: TextView
    private lateinit var tv_file_str_close: TextView
    private lateinit var sv_file_str: ScrollView
    private lateinit var tv_file_str: TextView
    private lateinit var pb_file_str_loading: ProgressBar
    private lateinit var ll_file_open: LinearLayout
    private lateinit var tv_file_open_text_by_self: TextView
    private lateinit var tv_file_open_text_by_system: TextView

    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_file

    override fun getPanelName(): String = "文件"

    override fun onPanelViewCreated(view: View) {
        rv_file_tab = view.findViewById(R.id.rv_file_tab)
        rv_file_navi = view.findViewById(R.id.rv_file_navi)
        tv_navi_length = view.findViewById(R.id.tv_navi_length)
        rv_file_name = view.findViewById(R.id.rv_file_name)
        ll_file_str = view.findViewById(R.id.ll_file_str)
        tv_file_str_name = view.findViewById(R.id.tv_file_str_name)
        tv_file_str_close = view.findViewById(R.id.tv_file_str_close)
        sv_file_str = view.findViewById(R.id.sv_file_str)
        tv_file_str = view.findViewById(R.id.tv_file_str)
        pb_file_str_loading = view.findViewById(R.id.pb_file_str_loading)
        ll_file_open = view.findViewById(R.id.ll_file_open)
        tv_file_open_text_by_self = view.findViewById(R.id.tv_file_open_text_by_self)
        tv_file_open_text_by_system = view.findViewById(R.id.tv_file_open_text_by_system)
        rv_file_tab.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_file_tab.adapter = fileTabAdapter
        tv_file_str_close.setOnClickListener {
            ll_file_str.visibility = View.GONE
            tv_file_str.text = ""
            FileManager.closeStrFile()
        }
        rv_file_navi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_file_navi.adapter = fileNaviAdapter
        rv_file_name.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_file_name.adapter = fileNameAdapter
    }

    override fun onVisible(firstVisible: Boolean) {
        super.onVisible(firstVisible)
        if (firstVisible) {
            fileTabAdapter.set(mutableListOf<FileManager.FileEntity>().apply {
                add(FileManager.getRootDataDir(context).apply {
                    name = "内部存储"
                })
                add(FileManager.getRootExternalDir(context).apply {
                    name = "外部存储"
                })
            })
        }
    }

    private fun onTabClick(fileEntity: FileManager.FileEntity) {
        val dataNavi = mutableListOf<FileManager.FileEntity>()
        dataNavi.add(fileEntity)
        fileNaviAdapter.set(dataNavi)
        onNaviClick(dataNavi[0])
    }

    private fun onNaviClick(fileEntity: FileManager.FileEntity) {
        tv_navi_length.text = fileEntity.formatLength(context)
        val datas = fileNaviAdapter.get()
        val newDatas = mutableListOf<FileManager.FileEntity>()
        for (data in datas) {
            newDatas.add(data)
            if (data.path == fileEntity.path) {
                break
            }
        }
        fileNaviAdapter.set(newDatas)
        val list = FileManager.childFilesOrNull(fileEntity)
        if (list == null) {
            fileNameAdapter.set(emptyList())
            return
        }
        fileNameAdapter.set(list)
    }

    private fun onNameClick(fileEntity: FileManager.FileEntity) {
        val list = FileManager.childFilesOrNull(fileEntity)
        if (list == null) {
            tv_file_str_name.text = fileEntity.name
            tv_file_str.text = ""
            ll_file_str.visibility = View.VISIBLE
            pb_file_str_loading.visibility = View.GONE
            sv_file_str.visibility = View.GONE
            ll_file_open.visibility = View.VISIBLE
            tv_file_open_text_by_self.setOnClickListener {
                pb_file_str_loading.visibility = View.VISIBLE
                sv_file_str.visibility = View.GONE
                ll_file_open.visibility = View.GONE
                FileManager.readStrFile(fileEntity) {
                    pb_file_str_loading.visibility = View.GONE
                    sv_file_str.visibility = View.VISIBLE
                    ll_file_open.visibility = View.GONE
                    tv_file_str.text = it
                }
            }
            tv_file_open_text_by_system.setOnClickListener {
                FileManager.openFile(context, fileEntity)
            }
            return
        }
        fileNaviAdapter.add(fileEntity)
        fileNameAdapter.set(list)
    }
}