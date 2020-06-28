package per.goweii.ponyo.startup

import android.os.AsyncTask

internal class AsyncRunner(
    private val runner: SyncRunner
) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg params: Unit?) {
        runner.execute()
    }
}