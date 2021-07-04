package per.goweii.ponyo.device;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public final class DeviceUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 判断设备是否 root
     *
     * @return the boolean{@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDeviceRooted() {
        try {
            String su = "su";
            String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                    "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
            for (String location : locations) {
                if (new File(location + su).exists()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取wifi的强弱
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static String getWifiState(Context context) {
        if (isWifiConnect(context)) {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null) {
                WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
                int wifi = mWifiInfo.getRssi();//获取wifi信号强度
                if (wifi > -50 && wifi < 0) {//最强
                    return "最强";
                } else if (wifi > -70 && wifi < -50) {//较强
                    return "较强";
                } else if (wifi > -80 && wifi < -70) {//较弱
                    return "较弱";
                } else if (wifi > -100 && wifi < -80) {//微弱
                    return "微弱";
                } else {
                    return "微弱";
                }
            }
        }
        return "无wifi连接";
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifiInfo.isConnected();
        }
        return false;
    }

    /**
     * 获取wifi的名称
     */
    public static String getWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        }
        return "无网络";
    }

    /**
     * 获取wifi的ip
     */
    public static String getWifiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return intToIp(wifiInfo.getIpAddress());
        }
        return "";
    }

    public static String getSDCardSpace(Context context) {
        try {
            String free = getSDAvailableSize(context);
            String total = getSDTotalSize(context);
            return free + "/" + total;
        } catch (Exception e) {
            return "-/-";
        }
    }

    /**
     * 获得SD卡总大小
     */
    public static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     */
    public static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    public static String getMemorySpace(Context context) {
        try {
            String free = getMemoryTotalSize(context);
            String total = getMemoryAvailSize(context);
            return free + "/" + total;
        } catch (Exception e) {
            return "-/-";
        }
    }

    /**
     * 手机总内存
     */
    public static String getMemoryTotalSize(Context context) {
        return Formatter.formatFileSize(context, getMemoryTotalLength());
    }

    /**
     * 手机总内存
     */
    public static long getMemoryTotalLength() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            if (!TextUtils.isEmpty(str2)) {
                arrayOfString = str2.split("\\s+");
                // 获得系统总内存，单位是KB，乘以1024转换为Byte
                initial_memory = Integer.parseInt(arrayOfString[1]) / 1024;
            }
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return initial_memory;
    }

    /**
     * 手机当前可用内存
     */
    public static String getMemoryAvailSize(Context context) {
        return Formatter.formatFileSize(context, getMemoryAvailLength(context));
    }

    /**
     * 手机当前可用内存
     *
     * @return 手机当前可用内存(兆)
     */
    public static long getMemoryAvailLength(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        if (am != null) {
            am.getMemoryInfo(mi);
        }
        return mi.availMem;
    }

    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("ObsoleteSdkInt")
    public static double getScreenInch(Context context) {
        double inch = 0;
        try {
            int realWidth = 0, realHeight = 0;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                realWidth = size.x;
                realHeight = size.y;
            } else if (Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } else {
                realWidth = metrics.widthPixels;
                realHeight = metrics.heightPixels;
            }
            inch = formatDouble(Math.sqrt((realWidth / metrics.xdpi) * (realWidth / metrics.xdpi)
                    + (realHeight / metrics.ydpi) * (realHeight / metrics.ydpi)), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inch;
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取设备 MAC 地址
     *
     * @return MAC 地址
     */
    @RequiresPermission(allOf = {
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.INTERNET",
    })
    public static String getMacAddress(Context context) {
        String macAddress = getMacAddressByWifiInfo(context);
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        return "please open wifi";
    }

    /**
     * 获取设备 MAC 地址
     *
     * @return MAC 地址
     */
    @RequiresPermission("android.permission.ACCESS_WIFI_STATE")
    @SuppressLint({"HardwareIds"})
    private static String getMacAddressByWifiInfo(Context context) {
        try {
            @SuppressLint("WifiManagerLeak")
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备 MAC 地址
     *
     * @return MAC 地址
     */
    @RequiresPermission("android.permission.INTERNET")
    private static String getMacAddressByNetworkInterface() {
        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nis) {
                if (!ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02x:", b));
                    }
                    return res1.deleteCharAt(res1.length() - 1).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备 MAC 地址
     *
     * @return MAC 地址
     */
    private static String getMacAddressByFile() {
        CommandResult result = execCmd(new String[]{"getprop wifi.interface"}, false, true);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = execCmd(new String[]{"cat /sys/class/net/" + name + "/address"}, false, true);
                if (result.result == 0) {
                    if (result.successMsg != null) {
                        return result.successMsg;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 是否是在 root 下执行命令
     *
     * @param commands        命令数组
     * @param isRoot          是否需要 root 权限执行
     * @param isNeedResultMsg 是否需要结果消息
     * @return CommandResult
     */
    public static CommandResult execCmd(final String[] commands,
                                        final boolean isRoot,
                                        final boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) continue;
                os.write(command.getBytes());
                os.writeBytes(LINE_SEP);
                os.flush();
            }
            os.writeBytes("exit" + LINE_SEP);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream(),
                        StandardCharsets.UTF_8));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                        StandardCharsets.UTF_8));
                String line;
                if ((line = successResult.readLine()) != null) {
                    successMsg.append(line);
                    while ((line = successResult.readLine()) != null) {
                        successMsg.append(LINE_SEP).append(line);
                    }
                }
                if ((line = errorResult.readLine()) != null) {
                    errorMsg.append(line);
                    while ((line = errorResult.readLine()) != null) {
                        errorMsg.append(LINE_SEP).append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(os, successResult, errorResult);
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(
                result,
                successMsg == null ? null : successMsg.toString(),
                errorMsg == null ? null : errorMsg.toString()
        );
    }

    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    private static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回的命令结果
     */
    public static class CommandResult {
        /**
         * 结果码
         **/
        public int result;
        /**
         * 成功信息
         **/
        public String successMsg;
        /**
         * 错误信息
         **/
        public String errorMsg;

        public CommandResult(final int result, final String successMsg, final String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
