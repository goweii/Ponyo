package per.goweii.ponyo.net.utils;

public final class NetLogUtils {
    private static final String TAG = "Ponyo-Net";

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    public static int LEVEL = Integer.MAX_VALUE;

    private static boolean $(String msg) {
        return msg == null;
    }

    public static void v(String tag, String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= VERBOSE) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= VERBOSE) {
            android.util.Log.v(TAG, msg);
        } 
    }

    public static void d(String tag, String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= INFO) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= INFO) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void w(String tag, String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= WARN) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= WARN) {
            android.util.Log.w(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= ERROR) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if ($(msg)) {
            return;
        }
        if (LEVEL <= ERROR) {
            android.util.Log.e(TAG, msg);
        }
    }

}
