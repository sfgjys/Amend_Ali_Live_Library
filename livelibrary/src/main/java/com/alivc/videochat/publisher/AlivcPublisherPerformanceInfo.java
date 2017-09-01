//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

public class AlivcPublisherPerformanceInfo {
    private int mAudioEncodeBitrate;
    private int mVideoEncodeBitrate;
    private int mAudioUploadBitrate;
    private int mVideoUploadBitrate;
    private int mAudioPacketsInBuffer;
    private int mVideoPacketsInBuffer;
    private int mVideoEncodedFps;
    private int mVideoUploadedFps;
    private int mVideoCaptureFps;
    private long mCurrentlyUploadedVideoFramePts;
    private long mCurrentlyUploadedAudioFramePts;
    private long mPreviousKeyFramePts;
    private long mTotalFramesOfEncodedVideo;
    private long mTotalTimeOfEncodedVideo;
    private long mTotalSizeOfUploadedPackets;
    private long mTotalTimeOfPublishing;
    private long mTotalFramesOfVideoUploaded;
    private long mDropDurationOfVideoFrames;
    private int mVideoDurationFromeCaptureToUpload;
    private int mAudioDurationFromeCaptureToUpload;

    public AlivcPublisherPerformanceInfo() {
    }

    public int getAudioEncodeBitrate() {
        return this.mAudioEncodeBitrate;
    }

    public void setAudioEncodeBitrate(int audioEncodeBitrate) {
        this.mAudioEncodeBitrate = audioEncodeBitrate;
    }

    public int getAudioUploadBitrate() {
        return this.mAudioUploadBitrate;
    }

    public void setAudioUploadBitrate(int audioUploadBitrate) {
        this.mAudioUploadBitrate = audioUploadBitrate;
    }

    public int getVideoUploadBitrate() {
        return this.mVideoUploadBitrate;
    }

    public void setVideoUploadBitrate(int videoUploadBitrate) {
        this.mVideoUploadBitrate = videoUploadBitrate;
    }

    public int getAudioPacketsInBuffer() {
        return this.mAudioPacketsInBuffer;
    }

    public void setAudioPacketsInBuffer(int audioPacketsInBuffer) {
        this.mAudioPacketsInBuffer = audioPacketsInBuffer;
    }

    public int getVideoPacketsInBuffer() {
        return this.mVideoPacketsInBuffer;
    }

    public void setVideoPacketsInBuffer(int videoPacketsInBuffer) {
        this.mVideoPacketsInBuffer = videoPacketsInBuffer;
    }

    public int getVideoDecodeFps() {
        return this.mVideoEncodedFps;
    }

    public void setVideoDecodeFps(int videoEncodeFps) {
        this.mVideoEncodedFps = videoEncodeFps;
    }

    public int getVideoUploadedFps() {
        return this.mVideoUploadedFps;
    }

    public void setVideoUploadedFps(int videoUploadedFps) {
        this.mVideoUploadedFps = videoUploadedFps;
    }

    public int getVideoCaptureFps() {
        return this.mVideoCaptureFps;
    }

    public void setVideoCaptureFps(int videoCaptureFps) {
        this.mVideoCaptureFps = videoCaptureFps;
    }

    public long getCurrentlyUploadedVideoFramePts() {
        return this.mCurrentlyUploadedVideoFramePts;
    }

    public void setCurrentlyUploadedVideoFramePts(long currentlyUploadedVideoFramePts) {
        this.mCurrentlyUploadedVideoFramePts = currentlyUploadedVideoFramePts;
    }

    public long getCurrentlyUploadedAudioFramePts() {
        return this.mCurrentlyUploadedAudioFramePts;
    }

    public void setCurrentlyUploadedAudioFramePts(long currentlyUploadedAudioFramePts) {
        this.mCurrentlyUploadedAudioFramePts = currentlyUploadedAudioFramePts;
    }

    public long getPreviousKeyFramePts() {
        return this.mPreviousKeyFramePts;
    }

    public void setPreviousKeyFramePts(long previousKeyFramePts) {
        this.mPreviousKeyFramePts = previousKeyFramePts;
    }

    public long getTotalFramesOfEncodedVideo() {
        return this.mTotalFramesOfEncodedVideo;
    }

    public void setTotalFramesOfEncodedVideo(long totalFramesOfEncodedVideo) {
        this.mTotalFramesOfEncodedVideo = totalFramesOfEncodedVideo;
    }

    public long getTotalTimeOfEncodedVideo() {
        return this.mTotalTimeOfEncodedVideo;
    }

    public void setTotalTimeOfEncodedVideo(long totalTimeOfEncodedVideo) {
        this.mTotalTimeOfEncodedVideo = totalTimeOfEncodedVideo;
    }

    public long getTotalSizeOfUploadedPackets() {
        return this.mTotalSizeOfUploadedPackets;
    }

    public void setTotalSizeOfUploadedPackets(long totalSizeOfUploadedPackets) {
        this.mTotalSizeOfUploadedPackets = totalSizeOfUploadedPackets;
    }

    public long getTotalTimeOfPublishing() {
        return this.mTotalTimeOfPublishing;
    }

    public void setTotalTimeOfPublishing(long totalTimeOfPublishing) {
        this.mTotalTimeOfPublishing = totalTimeOfPublishing;
    }

    public long getDropDurationOfVideoFrames() {
        return this.mDropDurationOfVideoFrames;
    }

    public void setDropDurationOfVideoFrames(long dropDurationOfVideoFrames) {
        this.mDropDurationOfVideoFrames = dropDurationOfVideoFrames;
    }

    public int getVideoEncodedFps() {
        return this.mVideoEncodedFps;
    }

    public void setVideoEncodedFps(int videoEncodedFps) {
        this.mVideoEncodedFps = videoEncodedFps;
    }

    public long getTotalFramesOfVideoUploaded() {
        return this.mTotalFramesOfVideoUploaded;
    }

    public void setTotalFramesOfVideoUploaded(long totalFramesOfVideoUploaded) {
        this.mTotalFramesOfVideoUploaded = totalFramesOfVideoUploaded;
    }

    public int getVideoDurationFromeCaptureToUpload() {
        return this.mVideoDurationFromeCaptureToUpload;
    }

    public void setVideoDurationFromeCaptureToUpload(int videoDurationFromeCaptureToUpload) {
        this.mVideoDurationFromeCaptureToUpload = videoDurationFromeCaptureToUpload;
    }

    public int getAudioDurationFromeCaptureToUpload() {
        return this.mAudioDurationFromeCaptureToUpload;
    }

    public void setAudioDurationFromeCaptureToUpload(int audioDurationFromeCaptureToUpload) {
        this.mAudioDurationFromeCaptureToUpload = audioDurationFromeCaptureToUpload;
    }

    public int getVideoEncodeBitrate() {
        return this.mVideoEncodeBitrate;
    }

    public void setVideoEncodeBitrate(int videoEncodeBitrate) {
        this.mVideoEncodeBitrate = videoEncodeBitrate;
    }
}
