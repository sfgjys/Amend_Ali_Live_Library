//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherPauseEvent {
    public static long mLastPauseTime = 0L;

    public PublisherPauseEvent() {
    }

    public static void sendEvent(PublisherPauseEvent.PublisherPauseArgs args, Context context) {
        mLastPauseTime = System.currentTimeMillis();
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("2003", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherPauseEvent.PublisherPauseArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(args.ts).append("&");
        sb.append("tt=").append(args.tt);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherPauseArgs {
        public long ts = 0L;
        public long tt = 0L;

        public PublisherPauseArgs() {
        }
    }
}
