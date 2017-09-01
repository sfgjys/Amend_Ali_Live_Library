//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

public class FrameUtil {
    private static FrameUtil.HandleFrame frameHandler;
    private static FrameUtil.DisplayFrame frameDisplayer;
    private static FrameUtil.InitFrame frameInitor;

    public FrameUtil() {
    }

    public static void handleFrame(long timestamp) {
        if(frameHandler != null) {
            frameHandler.handleFrame(timestamp);
        }

    }

    public static void renderFrame() {
        if(frameDisplayer != null) {
            frameDisplayer.renderFrame();
        }

    }

    public static void initRender(int width, int height) {
        if(frameInitor != null) {
            frameInitor.initRender(width, height);
        }

    }

    public static void setFrameHandler(FrameUtil.HandleFrame handler) {
        frameHandler = handler;
    }

    public static void setFrameDisplayer(FrameUtil.DisplayFrame handler) {
        frameDisplayer = handler;
    }

    public static void setFrameInitor(FrameUtil.InitFrame handler) {
        frameInitor = handler;
    }

    public interface DisplayFrame {
        void renderFrame();
    }

    public interface InitFrame {
        void initRender(int var1, int var2);
    }

    public interface HandleFrame {
        void handleFrame(long var1);
    }
}
