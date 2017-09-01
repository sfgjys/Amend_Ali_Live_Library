//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherStartEvent {
    public PublisherStartEvent() {
    }

    public static void sendEvent(PublisherStartEvent.PublisherStartArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("2001", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherStartEvent.PublisherStartArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("aut=").append(args.autMs).append("&");
        sb.append("vut=").append(args.vutMs);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherStartArgs {
        public long autMs = 0L;
        public long vutMs = 0L;

        public PublisherStartArgs() {
        }
    }
}
