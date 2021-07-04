package per.goweii.android.ponyo.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import per.goweii.ponyo.Ponyo

object OKHttpUtils {
    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            Ponyo.setupNet(this)
        }.build()
    }

    suspend fun get(
        url: String,
        onResponse: (String) -> Unit,
        onFailure: (Exception) -> Unit,
        onFinish: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .get()
                .url(url)
                .build()
            try {
                val response = okHttpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception(response.message)
                }
                val string = response.body?.string() ?: ""
                withContext(Dispatchers.Main) {
                    onResponse.invoke(string)
                    onFinish.invoke()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure.invoke(e)
                    onFinish.invoke()
                }
            }
        }
    }

    suspend fun post(
        url: String,
        params: Map<String, Any>,
        onResponse: (String) -> Unit,
        onFailure: (Exception) -> Unit,
        onFinish: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
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
            try {
                val response = okHttpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception(response.message)
                }
                val string = response.body?.string() ?: ""
                withContext(Dispatchers.Main) {
                    onResponse.invoke(string)
                    onFinish.invoke()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure.invoke(e)
                    onFinish.invoke()
                }
            }
        }
    }
}