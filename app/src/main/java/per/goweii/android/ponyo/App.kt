package per.goweii.android.ponyo

import android.app.Application
import android.content.Context
import per.goweii.ponyo.Ponyo
import per.goweii.ponyo.panel.Panel

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Ponyo.addPanel(object : Panel() {
            override fun getPanelName(): String {
                return "自定义面板"
            }

            override fun getLayoutRes(): Int {
                return R.layout.panel_test
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}