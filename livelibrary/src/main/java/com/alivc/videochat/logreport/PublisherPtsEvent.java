//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherPtsEvent {
    public PublisherPtsEvent() {
    }

    public static void sendEvent(PublisherPtsEvent.PublisherPtsArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("9005", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherPtsEvent.PublisherPtsArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("vpts=").append(args.vpts).append("&");
        sb.append("apts=").append(args.apts).append("&");
        sb.append("vbpts=").append(args.vbpts).append("&");
        sb.append("abpts=").append(args.abpts);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherPtsArgs {
        public long vpts = 0L;
        public long apts = 0L;
        public long vbpts = 0L;
        public long abpts = 0L;

        public PublisherPtsArgs() {
        }
    }
}
