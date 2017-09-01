//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.logreport;

import android.content.Context;

public class PublisherFpsEvent {
    public PublisherFpsEvent() {
    }

    public static void sendEvent(PublisherFpsEvent.PublisherFpsBeatArgs args, Context context) {
        String argsStr = getArgsStr(args);
        PublicPraram publicPraram = new PublicPraram(context);
        String finalUrl = publicPraram.getPublisherFinalUrl("9002", argsStr);
        EventUtils.sendUrl(finalUrl);
    }

    private static String getArgsStr(PublisherFpsEvent.PublisherFpsBeatArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("vef=").append(args.vef).append("&");
        sb.append("vuf=").append(args.vuf).append("&");
        sb.append("vcf=").append(args.vcf).append("&");
        sb.append("tf=").append(args.tf).append("&");
        sb.append("df=").append(args.df).append("&");
        sb.append("abf=").append(args.abf).append("&");
        sb.append("vbfs=").append(args.vbfs);
        return EventUtils.urlEncode(sb.toString());
    }

    public static class PublisherFpsBeatArgs {
        public long vef = 0L;
        public long vuf = 0L;
        public long vcf = 0L;
        public long tf = 0L;
        public long df = 0L;
        public long abf = 0L;
        public long vbfs = 0L;

        public PublisherFpsBeatArgs() {
        }
    }
}
