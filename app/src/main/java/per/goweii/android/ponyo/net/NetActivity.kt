package per.goweii.android.ponyo.net

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_net.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import per.goweii.android.ponyo.R

class NetActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net)
        btn_home_article.setOnClickListener {
            launch {
                pb_home_article.visibility = View.VISIBLE
                btn_home_article.visibility = View.INVISIBLE
                OKHttpUtils.get("https://www.wanandroid.com/article/list/0/json",
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_home_article.visibility = View.INVISIBLE
                    btn_home_article.visibility = View.VISIBLE
                }
            }
        }
        btn_banner.setOnClickListener {
            launch {
                pb_banner.visibility = View.VISIBLE
                btn_banner.visibility = View.INVISIBLE
                OKHttpUtils.get("https://www.wanandroid.com/banner/json",
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_banner.visibility = View.INVISIBLE
                    btn_banner.visibility = View.VISIBLE
                }
            }
        }
        btn_collect.setOnClickListener {
            launch {
                pb_collect.visibility = View.VISIBLE
                btn_collect.visibility = View.INVISIBLE
                OKHttpUtils.post("https://www.wanandroid.com/lg/collect/add/json",
                    mutableMapOf<String, Any>().apply {
                        put("title", "Github:Goweii")
                        put("author", "goweii")
                        put("link", "https://github.com/goweii")
                    },
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_collect.visibility = View.INVISIBLE
                    btn_collect.visibility = View.VISIBLE
                }
            }
        }
        btn_search.setOnClickListener {
            launch {
                pb_search.visibility = View.VISIBLE
                btn_search.visibility = View.INVISIBLE
                OKHttpUtils.post("https://www.wanandroid.com/article/query/0/json",
                    mutableMapOf<String, Any>().apply {
                        put("k", "kotlin")
                    },
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_search.visibility = View.INVISIBLE
                    btn_search.visibility = View.VISIBLE
                }
            }
        }
        btn_userinfo.setOnClickListener {
            launch {
                pb_userinfo.visibility = View.VISIBLE
                btn_userinfo.visibility = View.INVISIBLE
                OKHttpUtils.get("https://www.wanandroid.com/lg/coin/userinfo/json",
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_userinfo.visibility = View.INVISIBLE
                    btn_userinfo.visibility = View.VISIBLE
                }
            }
        }
        btn_error_url.setOnClickListener {
            launch {
                pb_error_url.visibility = View.VISIBLE
                btn_error_url.visibility = View.INVISIBLE
                OKHttpUtils.get("https://www.wanandroid.com/lg/coin/userinfo/json/error",
                    { toastSuccess() },
                    { toastFailure() }
                ) {
                    pb_error_url.visibility = View.INVISIBLE
                    btn_error_url.visibility = View.VISIBLE
                }
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
