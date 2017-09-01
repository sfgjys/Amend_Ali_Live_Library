//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import com.alivc.videochat.publisher.AlivcPublisherPerformanceInfo;

import java.util.Map;

public interface IVideoChat {
    String ALIVC_FILTER_PARAM_BEAUTY_ON = "alivc_filter_param_beauty_on";
    int ONLINE_CHAT_STATUS_HOSTPLAYER_STARTED = 1;
    int ONLINE_CHAT_STATUS_PUBLISH_STARTED = 2;
    int ONLINE_CHAT_STATUS_PLAYERS_STARTED = 4;
    int ONLINE_CHAT_STATUS_STARTED = 7;
    int OFFLINE_CHAT_STATUS_HOSTPLAYER_STARTED = 1;
    int OFFLINE_CHAT_STATUS_PUBLISH_STOPED = 2;
    int OFFLINE_CHAT_STATUS_PLAYERS_STOPED = 4;
    int OFFLINE_CHAT_STATUS_STOPED = 7;

    void switchCamera();

    void setPublisherMuteModeOn(boolean var1);

    void setFlashOn(boolean var1);

    void setAutoFocusOn(boolean var1);

    void zoomCamera(float var1);

    void focusCameraAtAdjustedPoint(float var1, float var2);

    String getSDKVersion();

    AlivcPublisherPerformanceInfo getPublisherPerformanceInfo();

    AlivcPlayerPerformanceInfo getPlayerPerformanceInfo(String var1);

    void setFilterParam(Map<String, String> var1);

    void setPlayerParam(Map<String, String> var1);

    void setPublisherParam(Map<String, String> var1);

    void setScalingMode(VideoScalingMode var1);

    public static enum PublishStatus {
        UNSTARTED(0),
        PREPARED(1),
        STARTED(2),
        STOPED(3),
        FINISHED(4);

        private int status;

        private PublishStatus(int status) {
            this.status = status;
        }
    }

    public static enum PlayStatus {
        UNSTARTED(0),
        STARTED(1),
        STOPED(2);

        private int status;

        private PlayStatus(int status) {
            this.status = status;
        }
    }

    public static enum ChatStatus {
        UNSTARTED(0),
        STARTING(1),
        STARTED(2),
        STOPPING(3),
        STOPED(4),
        ADDING(5),
        REMOVING(6);

        private int status;

        private ChatStatus(int status) {
            this.status = status;
        }
    }
}
