package per.goweii.android.ponyo.net

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_net.*
import kotlinx.coroutines.*
import per.goweii.android.ponyo.R

class NetActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net)
        btn_home_article.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.get("https://www.wanandroid.com/article/list/0/json",
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
        btn_banner.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.get("https://www.wanandroid.com/banner/json",
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
        btn_collect.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.post("https://www.wanandroid.com/lg/collect/add/json",
                    mutableMapOf<String, Any>().apply {
                        put("title", "Github:Goweii")
                        put("author", "goweii")
                        put("link", "https://github.com/goweii")
                    },
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
        btn_search.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.post("https://www.wanandroid.com/article/query/0/json",
                    mutableMapOf<String, Any>().apply {
                        put("k", "kotlin")
                    },
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
        btn_userinfo.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.get("https://www.wanandroid.com/lg/coin/userinfo/json",
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
        btn_error_url.setOnClickListener {
            launch(Dispatchers.IO) {
                OKHttpUtils.get("https://www.wanandroid.com/lg/coin/userinfo/json/error",
                    { toastSuccess() },
                    { toastFailure() }
                )
            }
        }
    }

    private fun toastSuccess() {
        launch {
            Toast.makeText(this@NetActivity, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toastFailure() {
        launch {
            Toast.makeText(this@NetActivity, "Failure", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
