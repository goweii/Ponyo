package per.goweii.ponyo.panel.net

import okhttp3.OkHttpClient
import per.goweii.ponyo.net.stetho.NetworkInterceptor
import per.goweii.ponyo.net.stetho.NetworkListener
import per.goweii.ponyo.net.weaknet.WeakNetworkInterceptor

object NetManager {
    fun setup(okHttpClientBuilder: OkHttpClient.Builder) {
        okHttpClientBuilder.eventListenerFactory(NetworkListener.get())
        okHttpClientBuilder.addNetworkInterceptor(NetworkInterceptor())
        okHttpClientBuilder.addNetworkInterceptor(WeakNetworkInterceptor())
    }
}