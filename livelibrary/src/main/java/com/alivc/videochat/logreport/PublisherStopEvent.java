//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherStopEvent {
    public PublisherStopEvent() {
    }

    public static void sendEvent(PublisherStopEvent.PublisherStopArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("2002", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherStopEvent.PublisherStopArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("tus=").append(args.tus).append("&");
        sb.append("tut=").append(args.tut);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherStopArgs {
        public long tus = 0L;
        public long tut = 0L;

        public PublisherStopArgs() {
        }
    }
}
