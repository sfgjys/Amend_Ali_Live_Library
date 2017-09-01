//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.alivc.videochat.player.HttpClientUtil;
import com.alivc.videochat.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class EventUtils {
    private static String TAG = "usertrace";

    public EventUtils() {
    }

    public static String urlEncode(String url) {
        try {
            if (url != null) {
                return URLEncoder.encode(url, "UTF-8");
            }
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
        }

        return "";
    }

    public static String getIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    private static String intToIp(int i) {
        return (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
    }

    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow((double) ((float) dm.widthPixels / dm.xdpi), 2.0D);
        double y = Math.pow((double) ((float) dm.heightPixels / dm.ydpi), 2.0D);
        double screenInches = Math.sqrt(x + y);
        return screenInches >= 6.0D;
    }

    public static String createUuid(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + Secure.getString(context.getContentResolver(), "android_id");
        UUID deviceUuid = new UUID((long) androidId.hashCode(), (long) tmDevice.hashCode() << 32 | (long) tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static void sendUrl(final String url) {
        LogUtil.d(TAG, "usertrace : url = " + url);
        (new Thread(new Runnable() {
            public void run() {
                try {
                    HttpClientUtil.doHttpGet(url);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        })).start();
    }
}
