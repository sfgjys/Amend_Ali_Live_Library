//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherErrorEvent {
    public PublisherErrorEvent() {
    }

    public static void sendEvent(PublisherErrorEvent.PublisherErrorArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("4001", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherErrorEvent.PublisherErrorArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("error_code=").append(args.error_code).append("&");
        sb.append("error_msg=").append(args.error_msg).append("&");
        sb.append("ts=").append(args.ts).append("&");
        sb.append("tt=").append(args.tt);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherErrorArgs {
        public long error_code = 0L;
        public String error_msg = "";
        public long ts = 0L;
        public long tt = 0L;

        public PublisherErrorArgs() {
        }
    }
}
