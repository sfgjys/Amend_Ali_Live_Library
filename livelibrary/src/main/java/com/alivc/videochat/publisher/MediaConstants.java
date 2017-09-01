//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

public class MediaConstants {
    public static final String PUBLISHER_PARAM_UPLOAD_TIMEOUT = "UploadTimeout";
    public static final String PUBLISHER_PARAM_CAMERA_POSITION = "CameraPosition";
    public static final String PUBLISHER_PARAM_AUDIO_SAMPLE_RATE = "AudioSampleRate";
    public static final String PUBLISHER_PARAM_AUDIO_FRAME_SIZE = "AudioFrameSize";
    public static final String PUBLISHER_PARAM_ORIGINAL_BITRATE = "InitBitrate";
    public static final String PUBLISHER_PARAM_MAX_BITRATE = "MaxBitrate";
    public static final String PUBLISHER_PARAM_MIN_BITRATE = "MinBitrate";
    public static final String PUBLISHER_PARAM_AUDIO_BITRATE = "AudioBitrate";
    public static final String PUBLISHER_PARAM_VIDEO_FPS = "VideoFps";
    public static final String PUBLISHER_PARAM_SCREEN_ROTATION = "Rotation";
    public static final String PUBLISHER_PARAM_FRONT_CAMERA_MIRROR = "FrontCameraMirror";
    public static final String PLAYER_PARAM_DOWNLOAD_TIMEOUT = "PlayerTimeout";
    public static final String PLAYER_PARAM_DROP_BUFFER_DURATION = "PlayerMaxBufferDuration";
    public static final String PLAYER_PARAM_ENABLE_NATIVE_LOG = "PlayerEnableNativeLog";
    public static final String PLAYER_PARAM_SCALING_MODE = "ScalingMode";
    public static final String PLAYER_PARAM_MUTE_MODE = "MuteMode";
    public static final String FILTER_PARAM_BEAUTY_ON = "BeautyOn";
    public static final String FILTER_PARAM_BEAUTY_WHITEN = "BeautyWhiten";
    public static final String FILTER_PARAM_BEAUTY_BUFFING = "BeautyBuffing";
    public static final boolean DEFAULT_VALUE_BOOLEAN_BEAUTY_ON = true;
    public static final int DEFAULT_VALUE_INT_BEAUTY_WHITEN = 0;
    public static final int DEFAULT_VALUE_INT_BEAUTY_BUFFING = 0;
    public static final String DEFAULT_VALUE_STRING_CAMERA_FACING;
    public static final int DEFAULT_VALUE_INT_PUBLISHER_UPLOAD_TIMEOUT = 8000;
    public static final int DEFAULT_VALUE_INT_AUDIO_SAMPLE_RATE = 32000;
    public static final int DEFAULT_VALUE_INT_AUDIO_FRAME_SIZE = 2972;
    public static final int DEFAULT_VALUE_INT_INIT_BITRATE = 200;
    public static final int DEFAULT_VALUE_INT_MAX_BITRATE = 260;
    public static final int DEFAULT_VALUE_INT_MIN_BITRATE = 200;
    public static final int DEFAULT_VALUE_INT_TARGET_BITRATE = 200;
    public static final int DEFAULT_VALUE_INT_AUDIO_BITRATE = 200;
    public static final int DEFAULT_VALUE_INT_FRONT_CAMERA_MIRROR = 1;
    public static final int DEFAULT_VALUE_INT_VIDEO_FPS = 25;
    public static final int DEFAULT_VALUE_INT_ROTATION = 0;
    public static final int DEFAULT_VALUE_INT_PLAYER_TIMEOUT = 15000;
    public static final int DEFAULT_VALUE_INT_PLAYER_MAX_BUFFER_DURATION = 1000;
    public static final boolean DEFAULT_VALUE_BOOLEAN_PLAYER_ENABLE_NATIVE_LOG = false;
    public static final boolean DEFAULT_VALUE_BOOLEAN_PLAYER_MUTE_MODE = false;
    public static final int DEFAULT_VALUE_INT_EXPOSURECOMPENSATION = 20;

    public MediaConstants() {
    }

    public static int getInt(String value, int defaultValue) {
        int result = defaultValue;

        try {
            result = Integer.parseInt(value);
        } catch (Throwable var4) {
            ;
        }

        return result;
    }

    public static String getString(String value, String defaultValue) {
        String result = defaultValue;
        if(value != null) {
            result = value;
        }

        return result;
    }

    public static boolean getBoolean(String value, boolean defaultValue) {
        boolean result = defaultValue;

        try {
            result = Boolean.parseBoolean(value);
        } catch (Throwable var4) {
            ;
        }

        return result;
    }

    static {
        DEFAULT_VALUE_STRING_CAMERA_FACING = MediaConstants.CameraFacing.CAMERA_FACING_FRONT.name();
    }

    public static enum DisplayRotation {
        DISPLAY_ROTATION_0(0),
        DISPLAY_ROTATION_90(90),
        DISPLAY_ROTATION_180(180),
        DISPLAY_ROTATION_270(270);

        private int rotation;

        private DisplayRotation(int rotation) {
            this.rotation = rotation;
        }
    }

    public static enum CameraFacing {
        CAMERA_FACING_BACK("back"),
        CAMERA_FACING_FRONT("front");

        private String facing;

        private CameraFacing(String facing) {
            this.facing = facing;
        }
    }
}
