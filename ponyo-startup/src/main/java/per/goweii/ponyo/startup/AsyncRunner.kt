package per.goweii.ponyo.startup

import android.app.Application
import android.os.AsyncTask

class AsyncRunner(
    private val application: Application,
    private val isMainProcess: Boolean,
    private val tasks: ArrayList<Initializer>
) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg params: Unit?) {
        tasks.sortBy { it.priority() }
        for (task in tasks) {
            try {
                task.initialize(application, isMainProcess)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}