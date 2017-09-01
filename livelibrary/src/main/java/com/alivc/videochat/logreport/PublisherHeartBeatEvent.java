//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherHeartBeatEvent {
    public PublisherHeartBeatEvent() {
    }

    public static void sendEvent(PublisherHeartBeatEvent.PublisherHeartBeatArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("9001", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherHeartBeatEvent.PublisherHeartBeatArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(args.ts).append("&");
        sb.append("tt=").append(args.tt);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherHeartBeatArgs {
        public long ts = 0L;
        public long tt = 0L;

        public PublisherHeartBeatArgs() {
        }
    }
}
