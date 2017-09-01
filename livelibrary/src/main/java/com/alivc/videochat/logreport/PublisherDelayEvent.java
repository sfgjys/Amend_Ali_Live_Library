//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherDelayEvent {
    public PublisherDelayEvent() {
    }

    public static void sendEvent(PublisherDelayEvent.PublisherDelayArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("9004", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherDelayEvent.PublisherDelayArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("aut=").append(args.aut).append("&");
        sb.append("vut=").append(args.vut);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherDelayArgs {
        public long aut = 0L;
        public long vut = 0L;

        public PublisherDelayArgs() {
        }
    }
}
