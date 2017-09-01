//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.os.Build;

public class PhoneAdapterUtil {
    public static final String BRAND_GOOGLE = "google";
    public static final String BRAND_MEIZU = "Meizu";
    public static final String BRAND_XIAOMI = "Xiaomi";
    public static final String BRAND_MEITU = "Meitu";
    public static final String MODEL_NEXUS_6P = "Nexus 6P";
    public static final String MODEL_M351 = "M351";
    public static final String MODEL_MI_NOTE_PRO = "MI NOTE Pro";
    public static final String MODEL_RED_3 = "Redmi Note 3";
    public static final String MODEL_MI_PAD = "MI PAD";
    public static final String MODEL_MEITU_M4 = "Meitu M4";

    public PhoneAdapterUtil() {
    }

    public static boolean isHwNexus6P() {
        return Build.MODEL.equalsIgnoreCase("Nexus 6P") && Build.BRAND.equalsIgnoreCase("google");
    }

    public static int getSkipMicOffset() {
        return Build.MODEL.equalsIgnoreCase("M351") && Build.BRAND.equalsIgnoreCase("Meizu")?150:(Build.MODEL.equalsIgnoreCase("MI NOTE Pro") && Build.BRAND.equalsIgnoreCase("Xiaomi")?150:(Build.MODEL.equalsIgnoreCase("Redmi Note 3") && Build.BRAND.equalsIgnoreCase("Xiaomi")?0:(Build.MODEL.equalsIgnoreCase("MI PAD") && Build.BRAND.equalsIgnoreCase("Xiaomi")?450:(Build.MODEL.equalsIgnoreCase("Meitu M4") && Build.BRAND.equalsIgnoreCase("Meitu")?0:0))));
    }
}
