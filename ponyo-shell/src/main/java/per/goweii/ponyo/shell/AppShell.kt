package per.goweii.ponyo.shell

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import per.goweii.ponyo.shell.client.ShellClient
import per.goweii.ponyo.shell.client.ShellCommand
import per.goweii.ponyo.shell.client.ShellSocketThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object AppShell {
    private val clientHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val result = msg.obj?.toString()
            if (!result.isNullOrEmpty()) {
                resultCallback?.invoke(result)
            }
        }
    }

    private var socketThread: ShellSocketThread? = null

    var forkByAdb = true

    val isConnect: Boolean get() = socketThread?.isAlive == true

    var resultCallback: ((String) -> Unit)? = null

    fun forkShellProcess(context: Context) {
        val dexFile = prepareDexFile(context)
        if (forkByAdb) return
        ShellCommand("app_process")
            .append("-Djava.class.path=${dexFile.path}")
            .append("/system/bin")
            .append("--nice-name=ponyoshell")
            .append("per.goweii.ponyo.shell.server.ShellServer")
            .exec()
    }

    fun connectShellProcess(callback: (String) -> Unit): Boolean {
        resultCallback = callback
        if (isConnect) {
            socketThread?.setCallback { postMsg(it) }
            return true
        }
        socketThread = ShellClient.connectServer()?.also { thread ->
            thread.setCallback { postMsg(it) }
            postMsg("**********************************")
            postMsg("********** Hello shell! **********")
            postMsg("**********************************")
        }
        return socketThread != null
    }

    fun exec(cmd: String) {
        val thread = socketThread ?: return
        if (cmd.isEmpty()) return
        if (thread.exec(cmd)) {
            postMsg(":/ $ $cmd")
        }
    }

    private fun postMsg(result: String?) {
        if (result.isNullOrEmpty()) return
        val handler = clientHandler
        val msg = handler.obtainMessage().also { it.obj = result }
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
}