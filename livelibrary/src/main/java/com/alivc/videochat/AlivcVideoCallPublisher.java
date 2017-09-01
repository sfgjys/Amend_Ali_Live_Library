//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.app.Activity;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceView;

import com.alivc.videochat.player.AccessKeyCallback;
import com.alivc.videochat.player.AliVcMediaPlayer;
import com.alivc.videochat.publisher.AlivcSurfaceView;
import com.alivc.videochat.publisher.AudioSource;
import com.alivc.videochat.publisher.AudioSource.AudioSourceListener;
import com.alivc.videochat.publisher.FrameUtil;
import com.alivc.videochat.publisher.FrameUtil.DisplayFrame;
import com.alivc.videochat.publisher.FrameUtil.HandleFrame;
import com.alivc.videochat.publisher.NativeVideoCallPublisher;
import com.alivc.videochat.publisher.VideoParam;
import com.alivc.videochat.publisher.VideoPusher;
import com.alivc.videochat.publisher.VideoPusher.VideoSourceListener;

public class AlivcVideoCallPublisher implements VideoCallPublisher {
    private static final String TAG = "JavaVideoCall";
    private static long sStartTime;
    private AliVcMediaPlayer mMediaplayer = null;
    private AudioSource mAudioSource = null;
    private VideoPusher mVideoPusher = null;
    private AlivcVideoCallPublisher.PublishStatus mStatus;
    private Activity mContext;

    public AlivcVideoCallPublisher() {
        this.mStatus = AlivcVideoCallPublisher.PublishStatus.UINITED;
        this.mContext = null;
    }

    public int initPublisher(Activity context) {
        this.mContext = context;
        this.mStatus = AlivcVideoCallPublisher.PublishStatus.INITED;
        return 0;
    }

    public int preparePublisher(final AlivcSurfaceView previewView, int width, int height, int rotation) {
        this.mAudioSource = new AudioSource();
        this.mAudioSource.setRecordParams(12, 32000, 2972);
        this.mAudioSource.setAudioSourceListener(new AudioSourceListener() {
            public void onAudioFrame(byte[] audioFrame, int length) {
                AlivcVideoCallPublisher.this.handleAudioFrame(audioFrame, length, System.nanoTime());
            }
        });

        try {
            this.mAudioSource.start();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        VideoParam videoParam = new VideoParam(width, height, 1024000, 25, 1, rotation);
        videoParam.setPushWidth(640);
        videoParam.setPushHeight(360);
        this.mVideoPusher = new VideoPusher(this.mContext, (Handler)null, videoParam);
        this.mVideoPusher.setVideoSourceListener(new VideoSourceListener() {
            public void onVideoFrame(byte[] videoFrame, int orientation) {
                AlivcVideoCallPublisher.this.handleVideoFrame(videoFrame, System.nanoTime());
            }
        });

        try {
            this.mVideoPusher.startPreview();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        if(previewView != null) {
            FrameUtil.setFrameHandler(new HandleFrame() {
                public void handleFrame(long timestamp) {
                    previewView.requestRender();
                }
            });
            FrameUtil.setFrameDisplayer(new DisplayFrame() {
                public void renderFrame() {
                    if(AlivcVideoCallPublisher.this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_STARTED || AlivcVideoCallPublisher.this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_VIDEOCALL_STARTED || AlivcVideoCallPublisher.this.mStatus == AlivcVideoCallPublisher.PublishStatus.PREPARED) {
                        NativeVideoCallPublisher.getInstance().renderEGLView();
                    }

                }
            });
        }

        this.mStatus = AlivcVideoCallPublisher.PublishStatus.PREPARED;
        return 0;
    }

    public int preparePublisher(Surface surface, int width, int height, int rotation) {
        NativeVideoCallPublisher.getInstance().setPreviewSurface(surface);
        this.mAudioSource = new AudioSource();
        this.mAudioSource.setRecordParams(12, 32000, 2972);
        this.mAudioSource.setAudioSourceListener(new AudioSourceListener() {
            public void onAudioFrame(byte[] audioFrame, int length) {
                AlivcVideoCallPublisher.this.handleAudioFrame(audioFrame, length, System.nanoTime());
            }
        });

        try {
            this.mAudioSource.start();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        VideoParam videoParam = new VideoParam(width, height, 1024000, 25, 1, rotation);
        videoParam.setPushWidth(640);
        videoParam.setPushHeight(360);
        this.mVideoPusher = new VideoPusher(this.mContext, (Handler)null, videoParam);
        this.mVideoPusher.setVideoSourceListener(new VideoSourceListener() {
            public void onVideoFrame(byte[] videoFrame, int orientation) {
                AlivcVideoCallPublisher.this.handleVideoFrame(videoFrame, System.nanoTime());
            }
        });

        try {
            this.mVideoPusher.startPreview();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        this.mStatus = AlivcVideoCallPublisher.PublishStatus.PREPARED;
        return 0;
    }

    public int startPublisher(String url) {
        NativeVideoCallPublisher.getInstance().startPublisher(url);
        this.mStatus = AlivcVideoCallPublisher.PublishStatus.PUBLISH_STARTED;
        return 0;
    }

    public int stopPublisher() {
        if(this.mStatus != AlivcVideoCallPublisher.PublishStatus.PUBLISH_STOPED) {
            NativeVideoCallPublisher.getInstance().stopPublisher();
            if(this.mAudioSource != null) {
                this.mAudioSource.stop();
            }

            this.mVideoPusher.stopPreview();
        }

        this.mStatus = AlivcVideoCallPublisher.PublishStatus.PUBLISH_STOPED;
        return 0;
    }

    public int releasePublisher() {
        this.stopVideoCall();
        this.stopPublisher();
        this.mStatus = AlivcVideoCallPublisher.PublishStatus.RELEASED;
        return 0;
    }

    public int startVideoCall(SurfaceView surface, String url) {
        AliVcMediaPlayer.init(this.mContext, "publisher", (AccessKeyCallback)null);
        this.mMediaplayer = new AliVcMediaPlayer(this.mContext, surface, 1);
        this.mMediaplayer.setDefaultDecoder(1);
        this.mMediaplayer.setMaxBufferDuration(1000);
        this.mMediaplayer.prepareAndPlay(url);
        this.mStatus = AlivcVideoCallPublisher.PublishStatus.PUBLISH_VIDEOCALL_STARTED;
        return 0;
    }

    public int stopVideoCall() {
        if(this.mMediaplayer != null && this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_VIDEOCALL_STARTED) {
            this.mMediaplayer.stop();
            this.mMediaplayer.destroy();
            this.mMediaplayer = null;
            this.mStatus = AlivcVideoCallPublisher.PublishStatus.PUBLISH_STARTED;
        }

        return 0;
    }

    private int handleVideoFrame(byte[] frame, long timestamp) {
        if(this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_STARTED || this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_VIDEOCALL_STARTED || this.mStatus == AlivcVideoCallPublisher.PublishStatus.PREPARED) {
            long start = System.currentTimeMillis();
            NativeVideoCallPublisher.getInstance().handleVideoFrame(frame, timestamp, 1, 0);
        }

        return 0;
    }

    private int handleAudioFrame(byte[] frame, int length, long timestamp) {
        if(this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_STARTED || this.mStatus == AlivcVideoCallPublisher.PublishStatus.PUBLISH_VIDEOCALL_STARTED || this.mStatus == AlivcVideoCallPublisher.PublishStatus.PREPARED) {
            NativeVideoCallPublisher.getInstance().handleAudioFrame(frame, length, timestamp);
        }

        return 0;
    }

    public void setSurfaceChanged() {
        this.mVideoPusher.setSurfaceChanged();
    }

    public void switchCamera() {
        try {
            this.mVideoPusher.switchCamera();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public String getDebugInfos() {
        return "";
    }

    static enum PublishStatus {
        UINITED(0),
        INITED(1),
        PREPARED(2),
        PUBLISH_STARTED(3),
        PUBLISH_VIDEOCALL_STARTED(4),
        PUBLISH_STOPED(4),
        RELEASED(5);

        private int status;

        private PublishStatus(int status) {
            this.status = status;
        }
    }
}
