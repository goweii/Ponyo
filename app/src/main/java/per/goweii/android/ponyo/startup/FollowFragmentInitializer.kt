package per.goweii.android.ponyo.startup

import android.app.Application
import per.goweii.android.ponyo.log.LogActivity
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Startup(
    activities = [
        "per.goweii.android.ponyo.appstack.ActiStackFragment"
    ]
)
class FollowFragmentInitializer : Initializer {
    override fun initialize(application: Application, isMainProcess: Boolean) {
        Ponlog.d { application.toString() }
    }
}