//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

public class AlivcPlayerPerformanceInfo {
    private long mVideoPacketsInBuffer;
    private long mAudioPacketsInBuffer;
    private long mVideoDurationFromDownloadToRender;
    private long mAudioDurationFromDownloadToRender;
    private long mVideoPtsOfLastPacketInBuffer;
    private long mAudioPtsOfLastPacketInBuffer;
    private double mLiveDiscardDuration;
    private long mDowloadSpeed;

    public AlivcPlayerPerformanceInfo() {
    }

    public long getVideoPacketsInBuffer() {
        return this.mVideoPacketsInBuffer;
    }

    public void setVideoPacketsInBuffer(long videoPacketsInBuffer) {
        this.mVideoPacketsInBuffer = videoPacketsInBuffer;
    }

    public long getAudioPacketsInBuffer() {
        return this.mAudioPacketsInBuffer;
    }

    public void setAudioPacketsInBuffer(long audioPacketsInBuffer) {
        this.mAudioPacketsInBuffer = audioPacketsInBuffer;
    }

    public long getVideoDurationFromDownloadToRender() {
        return this.mVideoDurationFromDownloadToRender;
    }

    public void setVideoDurationFromDownloadToRender(long videoDurationFromDownloadToRender) {
        this.mVideoDurationFromDownloadToRender = videoDurationFromDownloadToRender;
    }

    public long getAudioDurationFromDownloadToRender() {
        return this.mAudioDurationFromDownloadToRender;
    }

    public void setAudioDurationFromDownloadToRender(long audioDurationFromDownloadToRender) {
        this.mAudioDurationFromDownloadToRender = audioDurationFromDownloadToRender;
    }

    public long getVideoPtsOfLastPacketInBuffer() {
        return this.mVideoPtsOfLastPacketInBuffer;
    }

    public void setVideoPtsOfLastPacketInBuffer(long videoPtsOfLastPacketInBuffer) {
        this.mVideoPtsOfLastPacketInBuffer = videoPtsOfLastPacketInBuffer;
    }

    public long getAudioPtsOfLastPacketInBuffer() {
        return this.mAudioPtsOfLastPacketInBuffer;
    }

    public void setAudioPtsOfLastPacketInBuffer(long audioPtsOfLastPacketInBuffer) {
        this.mAudioPtsOfLastPacketInBuffer = audioPtsOfLastPacketInBuffer;
    }

    public double getLiveDiscardDuration() {
        return this.mLiveDiscardDuration;
    }

    public void setLiveDiscardDuration(double liveDiscardDuration) {
        this.mLiveDiscardDuration = liveDiscardDuration;
    }

    public long getDowloadSpeed() {
        return this.mDowloadSpeed;
    }

    public void setDowloadSpeed(long dowloadSpeed) {
        this.mDowloadSpeed = dowloadSpeed;
    }
}
