//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherBitrateEvent {
    public PublisherBitrateEvent() {
    }

    public static void sendEvent(PublisherBitrateEvent.PublisherBitrateArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("9003", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherBitrateEvent.PublisherBitrateArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("aeb=").append(args.aeb).append("&");
        sb.append("veb=").append(args.veb).append("&");
        sb.append("aub=").append(args.aub).append("&");
        sb.append("vub=").append(args.vub).append("&");
        sb.append("vepb=").append(args.vepb);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherBitrateArgs {
        public long aeb = 0L;
        public long veb = 0L;
        public long aub = 0L;
        public long vub = 0L;
        public long vepb = 0L;

        public PublisherBitrateArgs() {
        }
    }
}
