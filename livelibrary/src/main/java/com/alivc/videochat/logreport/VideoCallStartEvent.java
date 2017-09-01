//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class VideoCallStartEvent {
    public VideoCallStartEvent() {
    }

    public static void sendEvent(VideoCallStartEvent.VideoCallStartArgs args, Context context) {
        String argsStr = getArgsStr(args);
        VideoPublicPraram publicPraram = new VideoPublicPraram(context);
        String finalUrl = publicPraram.getMixerFinalUrl("2001", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(VideoCallStartEvent.VideoCallStartArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("target_url=").append(args.target_url);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class VideoCallStartArgs {
        public String target_url = "";

        public VideoCallStartArgs() {
        }
    }
}
