package per.goweii.ponyo.shell

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import per.goweii.ponyo.shell.client.ShellClient
import per.goweii.ponyo.shell.client.ShellCommand
import per.goweii.ponyo.shell.client.ShellConnectStateListener
import per.goweii.ponyo.shell.client.ShellSocketThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object AppShell {
    private val handler = H()
    private var thread: ShellSocketThread? = null

    val isConnected: Boolean get() = thread?.isAlive == true

    var onConnected: (() -> Unit)? = null
    var onDisconnect: (() -> Unit)? = null
    var onResult: ((String) -> Unit)? = null

    fun forkShellProcess(context: Context) {
        val dexFile = prepareDexFile(context)
        ShellCommand("nohup")
            .append("app_process")
            .append("-Djava.class.path=${dexFile.path}")
            .append("/system/bin")
            .append("--nice-name=ponyoshell")
            .append("per.goweii.ponyo.shell.server.ShellServer")
            .append(">/dev/null 2>&1 &")
            .exec()
    }

    fun connectShellProcess(): Boolean {
        if (!isConnected) {
            thread = ShellClient.connectServer()
            thread?.setResultCallback {
                postOnResult(it)
            }
            thread?.setConnectStateListener(object : ShellConnectStateListener {
                override fun onConnected() {
                    postOnConnected()
                }

                override fun onDisconnect() {
                    postOnDisconnect()
                }
            })
            thread?.start()
        }
        return thread != null
    }

    fun exec(cmd: String): Boolean {
        if (cmd.isEmpty()) return false
        val thread = thread ?: return false
        return thread.exec(cmd)
    }

    private fun postOnConnected() {
        handler.sendEmptyMessage(WHAT_ON_CONNECTED)
    }

    private fun postOnDisconnect() {
        handler.sendEmptyMessage(WHAT_ON_DISCONNECT)
    }

    private fun postOnResult(result: String?) {
        if (result.isNullOrEmpty()) return
        val msg = handler.obtainMessage(WHAT_ON_RESULT)
        msg.obj = result
        handler.sendMessage(msg)
    }

    private fun prepareDexFile(context: Context): File {
        val dexFile = File(context.filesDir, "shell_server.dex")
        if (dexFile.exists()) {
            dexFile.delete()
        }
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = context.assets.open("shell_server")
            outputStream = dexFile.outputStream()
            inputStream.copyTo(outputStream)
            outputStream.flush()
        } catch (e: Throwable) {
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
            }
        }
        return dexFile
    }

    private const val WHAT_ON_CONNECTED = 1
    private const val WHAT_ON_DISCONNECT = 2
    private const val WHAT_ON_RESULT = 3

    private class H : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_ON_CONNECTED -> {
                    onConnected?.invoke()
                }
                WHAT_ON_DISCONNECT -> {
                    onDisconnect?.invoke()
                }
                WHAT_ON_RESULT -> {
                    val result = msg.obj?.toString()
                    if (!result.isNullOrEmpty()) {
                        onResult?.invoke(result)
                    }
                }
            }
        }
    }
}