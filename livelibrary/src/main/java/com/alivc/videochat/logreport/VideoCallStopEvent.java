//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class VideoCallStopEvent {
    public VideoCallStopEvent() {
    }

    public static void sendEvent(VideoCallStopEvent.VideoCallStopArgs args, Context context) {
        String argsStr = getArgsStr(args);
        VideoPublicPraram publicPraram = new VideoPublicPraram(context);
        String finalUrl = publicPraram.getMixerFinalUrl("2002", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(VideoCallStopEvent.VideoCallStopArgs args) {
        StringBuilder sb = new StringBuilder();
        return EventUtils.urlEncode(sb.toString());
    }

    public static class VideoCallStopArgs {
        public VideoCallStopArgs() {
        }
    }
}
