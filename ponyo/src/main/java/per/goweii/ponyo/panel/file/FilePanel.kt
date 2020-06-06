package per.goweii.ponyo.panel.file

import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class FilePanel : BasePanel() {
    private val fileNaviAdapter by lazy {
        FileNaviAdapter { onNaviClick(it) }
    }
    private val fileNameAdapter by lazy {
        FileNameAdapter { onNameClick(it) }
    }
    private lateinit var rv_file_navi: RecyclerView
    private lateinit var rv_file_name: RecyclerView
    private lateinit var rl_file_str: RelativeLayout
    private lateinit var tv_file_str_name: TextView
    private lateinit var tv_file_str_close: TextView
    private lateinit var tv_file_str: TextView
    private lateinit var pb_file_str_loading: ProgressBar

    override fun getPanelLayoutRes(): Int = R.layout.panel_file

    override fun getPanelName(): String = "文件"

    override fun onPanelViewCreated(view: View) {
        rv_file_navi = view.findViewById(R.id.rv_file_navi)
        rv_file_name = view.findViewById(R.id.rv_file_name)
        rl_file_str = view.findViewById(R.id.rl_file_str)
        tv_file_str_name = view.findViewById(R.id.tv_file_str_name)
        tv_file_str_close = view.findViewById(R.id.tv_file_str_close)
        tv_file_str = view.findViewById(R.id.tv_file_str)
        pb_file_str_loading = view.findViewById(R.id.pb_file_str_loading)
        tv_file_str_close.setOnClickListener {
            FileManager.closeStrFile()
            pb_file_str_loading.visibility = View.INVISIBLE
            rl_file_str.visibility = View.GONE
            tv_file_str_name.text = null
            tv_file_str.text = null
        }
        rv_file_navi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_file_navi.adapter = fileNaviAdapter
        rv_file_name.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_file_name.adapter = fileNameAdapter
        init()
    }

    private fun init() {
        FileManager.getRootDataDir(context)
        val dataNavi = mutableListOf<FileManager.FileEntity>()
        dataNavi.add(FileManager.getRootDataDir(context))
        fileNaviAdapter.set(dataNavi)
        onNaviClick(dataNavi[0])
    }

    private fun onNaviClick(fileEntity: FileManager.FileEntity) {
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
            pb_file_str_loading.visibility = View.VISIBLE
            rl_file_str.visibility = View.VISIBLE
            tv_file_str_name.text = fileEntity.name
            FileManager.readStrFile(fileEntity) {
                pb_file_str_loading.visibility = View.INVISIBLE
                tv_file_str.text = it
            }
            return
        }
        fileNaviAdapter.add(fileEntity)
        fileNameAdapter.set(list)
    }
}