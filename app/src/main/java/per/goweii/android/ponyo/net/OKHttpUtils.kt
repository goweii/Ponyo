package per.goweii.android.ponyo.net

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import per.goweii.ponyo.Ponyo
import java.io.IOException

object OKHttpUtils {
    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            Ponyo.setupNet(this)
        }.build()
    }

    fun get(url: String, onResponse: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure.invoke(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onResponse.invoke(response.body?.string() ?: "")
                } else {
                    onFailure.invoke(Exception(response.message))
                }
            }
        })
    }

    fun post(
        url: String,
        params: Map<String, Any>,
        onResponse: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sb = StringBuilder()
        for (key in params.keys) {
            sb.append(key)
                .append("=")
                .append("")
                .append(params[key])
                .append("&")
        }
        val requestBody = RequestBody.create(
            "application/x-www-form-urlencoded".toMediaType(), sb.toString()
        )
        val request = Request.Builder()
            .post(requestBody)
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure.invoke(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onResponse.invoke(response.body?.string() ?: "")
                } else {
                    onFailure.invoke(Exception(response.message))
                }
            }
        })
    }
}