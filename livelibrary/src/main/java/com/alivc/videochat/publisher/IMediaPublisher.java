//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.content.Context;
import android.view.Surface;

import com.alivc.videochat.VideoScalingMode;
import com.alivc.videochat.publisher.MediaConstants.CameraFacing;

import java.util.Map;

public interface IMediaPublisher {
    int init(Context var1);

    int prepare(Surface var1, int var2, int var3, Map<String, String> var4);

    int start(String var1);

    int stop();

    int reset();

    int release();

    void switchCamera();

    void setBeautyOn(boolean var1);

    void setSilentOn(boolean var1);

    void setBlackScreenOn(boolean var1);

    void setFlashOn(boolean var1);

    void setAutoFocusOn(boolean var1);

    void setBlurOn(boolean var1);

    void setZoom(float var1);

    void pause();

    void resume(Surface var1);

    void setFilterParam(Map var1);

    void setFocus(float var1, float var2);

    String getVersion();

    void enablePerformanceInfo();

    void disablePerformanceInfo();

    AlivcPublisherPerformanceInfo getPerformanceInfo();

    void setVideoScalingMode(VideoScalingMode var1);

    void setVideocall(boolean var1);

    public interface OnNetworkStatusChangedListener {
        void onNetworkBusy();

        void onNetworkFree();

        void onConnectionStatusChange(int var1);

        boolean onNetworkReconnect();
    }

    public interface OnPublisherStatusChangedListener {
        void onCameraOpened();

        void onCameraOpenFailed(CameraFacing var1);

        void onPreviewStarted();

        void onPreviewStoped();

        void onCameraClosed();

        void onIllegalOutputResolution();
    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    public interface OnInfoListener {
        boolean onInfo(IMediaPublisher var1, int var2, String var3);
    }

    public interface OnErrorListener {
        boolean onError(IMediaPublisher var1, int var2, String var3);
    }

    public static enum PublishStatus {
        UINITED(0),
        INITED(1),
        PREPARED(2),
        PUBLISH_STARTED(3),
        PUBLISH_STOPED(4),
        RELEASED(5);

        private int status;

        private PublishStatus(int status) {
            this.status = status;
        }
    }

    public static enum NetworkStatus {
        ;

        private NetworkStatus() {
        }
    }
}
