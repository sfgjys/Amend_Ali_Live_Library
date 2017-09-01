//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

public class VideoParam {
    private int width;
    private int height;
    private int pushWidth;
    private int pushHeight;
    private int cameraId;
    private int fps;
    private int bitrate;
    private int rotation;
    private int maxZoom;
    private int minZoom = 0;
    private int currentZoom = 1;

    public VideoParam(int width, int height, int bitrate, int fps, int cameraId, int rotation) {
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.fps = fps;
        this.cameraId = cameraId;
        this.rotation = rotation;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getPushWidth() {
        return this.pushWidth;
    }

    public void setPushWidth(int pushWidth) {
        this.pushWidth = pushWidth;
    }

    public int getPushHeight() {
        return this.pushHeight;
    }

    public void setPushHeight(int pushHeight) {
        this.pushHeight = pushHeight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getFps() {
        return this.fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBitrate() {
        return this.bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getMaxZoom() {
        return this.maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }

    public int getMinZoom() {
        return this.minZoom;
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    public int getCurrentZoom() {
        return this.currentZoom;
    }

    public void setCurrentZoom(int currentZoom) {
        this.currentZoom = currentZoom;
    }
}
