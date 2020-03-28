package per.goweii.ponyo

import android.app.Application

object Ponyo {

    private lateinit var application: Application

    fun attach(app: Application) = apply {
        application = app
    }

    fun showFloat(iconResId: Int) {
        FloatManager(application)
            .icon(iconResId)
            .show()
    }
}