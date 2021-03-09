package per.goweii.ponyo.net.utils;

import android.app.Application;

import okhttp3.OkHttpClient;
import per.goweii.ponyo.net.NetworkManager;
import per.goweii.ponyo.net.stetho.NetworkInterceptor;
import per.goweii.ponyo.net.stetho.NetworkListener;
import per.goweii.ponyo.net.weaknet.WeakNetworkInterceptor;

public class NetworkTool {
    private static NetworkTool INSTANCE;
    private Application app;

    public static NetworkTool getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkTool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkTool();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Application application) {
        this.app = application;
        setOkHttpHook();
        NetworkManager.get().startMonitor();
    }

    public void stopMonitor() {
        NetworkManager.get().stopMonitor();
    }

    public Application getApplication() {
        return app;
    }

    public OkHttpClient.Builder addOkHttp(OkHttpClient.Builder builder) {
        NetLogUtils.i("OkHttpHook" + "-------addOkHttp");
        return builder
                .eventListenerFactory(NetworkListener.get())
                .addNetworkInterceptor(new WeakNetworkInterceptor())
                .addNetworkInterceptor(new NetworkInterceptor());
    }

    public void setOkHttpHook() {
        OkHttpHooker.installEventListenerFactory(NetworkListener.get());
        OkHttpHooker.installInterceptor(new NetworkInterceptor());
    }
}
