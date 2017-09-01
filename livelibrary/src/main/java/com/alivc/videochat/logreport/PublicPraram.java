//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;
import android.os.Build;

import java.util.UUID;

public class PublicPraram {
    private static String reportHost = "https://videocloud.cn-hangzhou.log.aliyuncs.com/logstores/pubchat/track?APIVersion=0.6.0&";
    private String time = "";
    private String log_level = "";
    private String log_version = "";
    private String product = "";
    private static String module = "";
    private String hostname = "";
    private static String business_id = "";
    private static String request_id = "";
    private String event = "";
    private String args = "";
    private static String video_type = "live";
    private String terminal_type = "";
    private String device_model = "";
    private String app_version = "";
    private String uuid = "";
    private static String video_url = "";
    private static String user_account = "0";
    private String definition = "hd";
    private String cdn_ip = "0.0.0.0";
    private String referer = "";

    public PublicPraram(Context context) {
        this.time = System.currentTimeMillis() + "";
        this.log_level = "info";
        this.log_version = "1";
        this.product = "mixer";
        this.hostname = EventUtils.getIp(context);
        this.terminal_type = this.getTerminalType(context);
        this.device_model = Build.MODEL;
        this.app_version = "1.0";
        this.uuid = EventUtils.createUuid(context);
        this.definition = "hd";
        this.cdn_ip = "0.0.0.0";
        this.referer = "aliyun";
    }

    public static void setHost(String logURL) {
        reportHost = logURL;
    }

    public static void setUserId(String userId) {
        user_account = userId;
        com.alivc.videochat.player.logreport.PublicPraram.setUserId(userId);
    }

    public static void setModule(String md) {
        module = md;
        com.alivc.videochat.player.logreport.PublicPraram.setModule(md);
    }

    private String getTerminalType(Context context) {
        boolean isPad = EventUtils.isPad(context);
        return isPad?"pad":"phone";
    }

    public static void setVideoType(PublicPraram.VideoType videoType) {
        video_type = videoType.name();
        com.alivc.videochat.player.logreport.PublicPraram.setVideoType(video_type);
    }

    public static void changeRequestId() {
        request_id = UUID.randomUUID().toString();
        com.alivc.videochat.player.logreport.PublicPraram.setRequestId(request_id);
    }

    public static void resetRequestId() {
        request_id = null;
        com.alivc.videochat.player.logreport.PublicPraram.resetRequestId();
    }

    public static void setBusinessId(String businessId) {
        com.alivc.videochat.player.logreport.PublicPraram.setBusinessId(businessId);
    }

    public static void setVideoUrl(String videoUrl) {
        video_url = videoUrl;
        com.alivc.videochat.player.logreport.PublicPraram.setVideoUrl(videoUrl);
        changeRequestId();
    }

    private String getParam(String event, String argsStr, String module) {
        StringBuilder finalSb = new StringBuilder();
        finalSb.append("t=").append(EventUtils.urlEncode(this.time)).append("&");
        finalSb.append("ll=").append(EventUtils.urlEncode(this.log_level)).append("&");
        finalSb.append("lv=").append(EventUtils.urlEncode(this.log_version)).append("&");
        finalSb.append("pd=").append(EventUtils.urlEncode(this.product)).append("&");
        finalSb.append("md=").append(EventUtils.urlEncode(module)).append("&");
        finalSb.append("hn=").append(EventUtils.urlEncode(this.hostname)).append("&");
        finalSb.append("bi=").append(EventUtils.urlEncode(business_id)).append("&");
        finalSb.append("ri=").append(EventUtils.urlEncode(request_id)).append("&");
        finalSb.append("e=").append(event).append("&");
        finalSb.append("args=").append(argsStr).append("&");
        finalSb.append("vt=").append(EventUtils.urlEncode(video_type)).append("&");
        finalSb.append("tt=").append(EventUtils.urlEncode(this.terminal_type)).append("&");
        finalSb.append("dm=").append(EventUtils.urlEncode(this.device_model)).append("&");
        finalSb.append("av=").append(EventUtils.urlEncode(this.app_version)).append("&");
        finalSb.append("uuid=").append(EventUtils.urlEncode(this.uuid)).append("&");
        finalSb.append("vu=").append(EventUtils.urlEncode(video_url)).append("&");
        finalSb.append("ua=").append(EventUtils.urlEncode(user_account)).append("&");
        finalSb.append("dn=").append(EventUtils.urlEncode(this.definition)).append("&");
        finalSb.append("cdn_ip=").append(EventUtils.urlEncode(this.cdn_ip)).append("&");
        finalSb.append("r=").append(EventUtils.urlEncode(this.referer));
        return finalSb.toString();
    }

    public String getPublisherFinalUrl(String event, String argsStr) {
        return reportHost + this.getParam(event, argsStr, "publisher");
    }

    public String getMixerFinalUrl(String event, String argsStr) {
        return reportHost + this.getParam(event, argsStr, "mixer");
    }

    public static enum VideoType {
        live,
        vod;

        private VideoType() {
        }
    }
}
