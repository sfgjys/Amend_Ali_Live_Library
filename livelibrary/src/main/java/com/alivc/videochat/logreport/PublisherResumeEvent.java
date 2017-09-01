//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherResumeEvent {
    public PublisherResumeEvent() {
    }

    public static void sendEvent(PublisherResumeEvent.PublisherResumeArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("2010", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherResumeEvent.PublisherResumeArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(args.ts).append("&");
        sb.append("tt=").append(args.tt).append("&");
        sb.append("cost=").append(args.cost);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherResumeArgs {
        public long ts = 0L;
        public long tt = 0L;
        public long cost = 0L;

        public PublisherResumeArgs() {
        }
    }
}
