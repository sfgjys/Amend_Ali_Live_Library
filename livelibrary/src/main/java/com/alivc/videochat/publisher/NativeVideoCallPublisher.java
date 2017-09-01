//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.view.Surface;

import com.alivc.videochat.utils.LogUtil;

public class NativeVideoCallPublisher {
    private static NativeVideoCallPublisher.NotificationListener sNotificationListener;
    public static final int PROP_INTEGER_ENCODE_SPEED = 10001;
    public static final int PROP_INTEGER_PUSH_SPEED = 10002;
    public static final int PROP_INTEGER_VIDEO_QUEUE_NUMBER = 10003;
    public static final int PROP_INTEGER_AUDIO_QUEUE_NUMBER = 10004;
    public static final int PROP_INTEGER_ENCODE_CRF = 10005;
    public static final int PROP_INTEGER_ENCODE_CRF_WARN = 10006;
    private static NativeVideoCallPublisher instance;

    public NativeVideoCallPublisher() {
    }

    public static void setNotificationListener(NativeVideoCallPublisher.NotificationListener notificationListener) {
        sNotificationListener = notificationListener;
    }

    public static NativeVideoCallPublisher getInstance() {
        if(instance == null) {
            instance = new NativeVideoCallPublisher();
        }

        return instance;
    }

    public static void handleFrame(long timestamp) {
        FrameUtil.handleFrame(timestamp);
    }

    public static void onNotification(int what, int arg0, int arg1, int objId) {
        LogUtil.d("", "notify erro in on notification r: " + what);
        if(sNotificationListener != null) {
            sNotificationListener.onNotification(what, arg0, arg1, objId);
        }

    }

    public native void setPreviewSurface(Surface var1);

    public native int preparePublisher(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

    public native int startPublisher(String var1);

    public native int stopPublisher();

    public native void resumePublisher(Surface var1);

    public native void pausePublisher();

    public native int releasePublisher();

    public native int handleVideoFrame(byte[] var1, long var2, int var4, int var5);

    public native int handleAudioFrame(byte[] var1, int var2, long var3);

    public native void initEGLView(int var1, int var2);

    public native void renderEGLView();

    public native void setBeautyOn(boolean var1);

    public native String getPerformanceInfo();

    public native void setPreviewScallingMode(int var1);

    public native void setPublishParam(int var1, int var2, int var3, int var4, int var5, int var6);

    public native void setVideocall(boolean var1);

    public native void setHeadsetOn(boolean var1);

    static {
        try {
            System.loadLibrary("Facebeauty");
            System.loadLibrary("videochat-rtmp");
            System.loadLibrary("videochat-openh264");
            System.loadLibrary("videochat-fdkaac");
            System.loadLibrary("videochat-ffmpeg");
            System.loadLibrary("videochat-player");
            System.loadLibrary("videochat-publisher");
        } catch (Throwable var1) {
            var1.printStackTrace();
        }

    }

    interface NotificationListener {
        void onNotification(int var1, int var2, int var3, int var4);
    }
}
