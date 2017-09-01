//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.videochat.logreport.PublicPraram;
import com.alivc.videochat.logreport.PublisherErrorEvent;
import com.alivc.videochat.logreport.PublisherErrorEvent.PublisherErrorArgs;
import com.alivc.videochat.logreport.PublisherPauseEvent;
import com.alivc.videochat.logreport.PublisherPauseEvent.PublisherPauseArgs;
import com.alivc.videochat.logreport.PublisherResumeEvent;
import com.alivc.videochat.logreport.PublisherResumeEvent.PublisherResumeArgs;
import com.alivc.videochat.logreport.VideoCallStartEvent;
import com.alivc.videochat.logreport.VideoCallStartEvent.VideoCallStartArgs;
import com.alivc.videochat.logreport.VideoCallStopEvent;
import com.alivc.videochat.logreport.VideoCallStopEvent.VideoCallStopArgs;
import com.alivc.videochat.logreport.VideoPublicPraram;
import com.alivc.videochat.player.AccessKeyCallback;
import com.alivc.videochat.player.AliVcMediaPlayer;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerCompletedListener;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerErrorListener;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerInfoListener;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerPreparedListener;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerStartedListener;
import com.alivc.videochat.player.MediaPlayer.MediaPlayerStopedListener;
import com.alivc.videochat.player.MediaPlayer.WorkMode;
import com.alivc.videochat.publisher.AlivcMediaPublisher;
import com.alivc.videochat.publisher.AlivcPublisherPerformanceInfo;
import com.alivc.videochat.publisher.IMediaPublisher;
import com.alivc.videochat.publisher.IMediaPublisher.OnPreparedListener;
import com.alivc.videochat.publisher.MediaConstants;
import com.alivc.videochat.publisher.NativeVideoCallPublisher;
import com.alivc.videochat.utils.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AlivcVideoChatHost implements IVideoChatHost {
    public static final String WARNTAG = "AlivcVideoChatHostWarn";
    public static final String TAG = "AlivcVideoChatHost";
    private AlivcMediaPublisher mMediaPublisher;
    private HostResumer mResumer = new HostResumer();
    private Map<String, AliVcMediaPlayer> mMediaPlayerMap = new HashMap();
    private Map<String, SurfaceView> mSurfaceViewMap = new HashMap();
    private Map<String, Callback> mStopCallbackMap = new HashMap();
    private Map<SurfaceView, AliVcMediaPlayer> mFreeMediaPlayerMap = new HashMap();
    private ChatStatus mChatStatus;
    private PublishStatus mPublishStatus;
    private Context mContext;
    private Map<String, String> mPlayParam;
    private Map<String, String> mPublishParam;
    private OnErrorListener mErrorListener;
    private OnInfoListener mInfoListener;
    private String mPlayUrl;
    private int mSoundManagerId;
    private com.alivc.videochat.publisher.IMediaPublisher.OnErrorListener mPublishErrorListener;
    private com.alivc.videochat.publisher.IMediaPublisher.OnInfoListener mOnInfoListener;
    private MediaPlayerErrorListener mPlayerErrorListener;
    private MediaPlayerPreparedListener mPlayerPreparedListener;
    private MediaPlayerCompletedListener mMediaPlayerCompletedListener;
    private MediaPlayerInfoListener mMediaPlayerInfoListener;
    private MediaPlayerStopedListener mMediaPlayerStopedListener;

    public AlivcVideoChatHost() {
        this.mChatStatus = ChatStatus.UNSTARTED;
        this.mPublishStatus = PublishStatus.UNSTARTED;
        this.mPlayParam = null;
        this.mPublishParam = null;
        this.mSoundManagerId = -1;
        this.mPublishErrorListener = new com.alivc.videochat.publisher.IMediaPublisher.OnErrorListener() {
            public boolean onError(IMediaPublisher mp, int errCode, String extra) {
                Log.d("AlivcVideoChatHost", "publish error: " + errCode);
                PublisherErrorArgs args = new PublisherErrorArgs();
                Map map = AlivcMediaPublisher.getPerformanceMap();
                args.ts = AlivcMediaPublisher.getLong(map, "mTotalSizeOfUploadedPackets");
                args.tt = AlivcMediaPublisher.getLong(map, "mTotalTimeOfPublishing");
                args.error_code = (long)errCode;
                args.error_msg = extra;
                PublisherErrorEvent.sendEvent(args, AlivcVideoChatHost.this.mContext);
                if(AlivcVideoChatHost.this.mErrorListener != null) {
                    AlivcVideoChatHost.this.mErrorListener.onError(AlivcVideoChatHost.this, errCode, extra);
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.mOnInfoListener = new com.alivc.videochat.publisher.IMediaPublisher.OnInfoListener() {
            public boolean onInfo(IMediaPublisher mp, int errCode, String extra) {
                LogUtil.d("AlivcVideoChatHost", "publish info: " + errCode);
                if(AlivcVideoChatHost.this.mInfoListener != null) {
                    AlivcVideoChatHost.this.mInfoListener.onInfo(AlivcVideoChatHost.this, errCode, extra);
                }

                return true;
            }
        };
        this.mPlayerErrorListener = new MediaPlayerErrorListener() {
            public void onError(int what, int extra, String url) {
                if(what == 401 || what == 504) {
                    what = 400;
                }

                LogUtil.d("AlivcVideoChatHost", "player error: " + what);
                if(AlivcVideoChatHost.this.mErrorListener != null) {
                    AlivcVideoChatHost.this.mErrorListener.onError(AlivcVideoChatHost.this, what, url);
                }

            }
        };
        this.mPlayerPreparedListener = new MediaPlayerPreparedListener() {
            public void onPrepared(final String url) {
                (new Thread(new Runnable() {
                    public void run() {
                        Iterator i$;
                        AliVcMediaPlayer player;
                        if(AlivcVideoChatHost.this.mPublishStatus != PublishStatus.PREPARED && AlivcVideoChatHost.this.mPublishStatus != PublishStatus.STARTED) {
                            i$ = AlivcVideoChatHost.this.mMediaPlayerMap.values().iterator();

                            while(i$.hasNext()) {
                                player = (AliVcMediaPlayer)i$.next();
                                player.setWorkMode(WorkMode.WORK_AS_PLAYER);
                            }
                        } else {
                            i$ = AlivcVideoChatHost.this.mMediaPlayerMap.values().iterator();

                            while(i$.hasNext()) {
                                player = (AliVcMediaPlayer)i$.next();
                                player.setWorkMode(WorkMode.WORK_AS_VIDEOCHAT);
                            }
                        }

                        if(AlivcVideoChatHost.this.mInfoListener != null) {
                            AlivcVideoChatHost.this.mInfoListener.onInfo(AlivcVideoChatHost.this, 150, url);
                        }

                    }
                })).start();
                LogUtil.d("AlivcVideoChatHost", "player info: 150");
            }
        };
        this.mMediaPlayerCompletedListener = new MediaPlayerCompletedListener() {
            public void onCompleted(String url) {
                if(AlivcVideoChatHost.this.mInfoListener != null) {
                    AlivcVideoChatHost.this.mInfoListener.onInfo(AlivcVideoChatHost.this, 151, url);
                }

                LogUtil.d("AlivcVideoChatHost", "player info: 151");
            }
        };
        this.mMediaPlayerInfoListener = new MediaPlayerInfoListener() {
            public void onInfo(int i, int i1, String url) {
                if(AlivcVideoChatHost.this.mInfoListener != null) {
                    AlivcVideoChatHost.this.mInfoListener.onInfo(AlivcVideoChatHost.this, i, url);
                }

                LogUtil.d("AlivcVideoChatHost", "player info: " + i);
            }
        };
        this.mMediaPlayerStopedListener = new MediaPlayerStopedListener() {
            public void onStopped(String url) {
                if(AlivcVideoChatHost.this.mInfoListener != null) {
                    AlivcVideoChatHost.this.mInfoListener.onInfo(AlivcVideoChatHost.this, 152, url);
                }

                if(AlivcVideoChatHost.this.mStopCallbackMap.containsKey(url)) {
                    Callback callback = (Callback)AlivcVideoChatHost.this.mStopCallbackMap.get(url);
                    callback.onEvent();
                }

                LogUtil.d("AlivcVideoChatHost", "player info: 152");
            }
        };
        LogUtil.d("AlivcVideoChatHostWarn", "new.");
        Random random = new Random();
        this.mMediaPublisher = new AlivcMediaPublisher();
        this.mMediaPublisher.setOnErrorListener(this.mPublishErrorListener);
        this.mMediaPublisher.setOnInfoListener(this.mOnInfoListener);
        this.mSoundManagerId = random.nextInt(10000);
    }

    public int init(Context context) {
        LogUtil.d("AlivcVideoChatHostWarn", "init.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else {
            this.mContext = context;
            return this.mMediaPublisher.init(context);
        }
    }

    public synchronized int prepareToPublish(SurfaceView previewSurface, int width, int height, Map<String, String> publisherParam) {
        LogUtil.d("AlivcVideoChatHostWarn", "prepareToPublish.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else if(previewSurface == null) {
            return -200;
        } else {
            int result = this.mMediaPublisher.prepare(previewSurface.getHolder().getSurface(), width, height, publisherParam);
            this.mPublishStatus = PublishStatus.PREPARED;
            this.mResumer.setPublishSurfaceHolder(previewSurface.getHolder());
            return result;
        }
    }

    public synchronized int startToPublish(String url) {
        LogUtil.d("AlivcVideoChatHostWarn", "startToPublish.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else if(url != null && !url.trim().equals("")) {
            PublicPraram.setModule("publisher");
            if(this.mPublishStatus == PublishStatus.PREPARED) {
                this.mPublishStatus = PublishStatus.STARTED;
                int result = this.mMediaPublisher.start(url);
                if(result == 0 && this.mInfoListener != null) {
                    this.mInfoListener.onInfo(this, -505, url);
                }

                return result;
            } else {
                return -200;
            }
        } else {
            return -200;
        }
    }

    public synchronized int stopPublishing() {
        LogUtil.d("AlivcVideoChatHostWarn", "stopPublishing.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else {
            if(this.mChatStatus == ChatStatus.STARTED) {
                this.abortChat();
                this.mPublishStatus = PublishStatus.STOPED;
                this.mResumer.setPublishSurfaceHolder((SurfaceHolder)null);
            }

            return this.mPublishStatus != PublishStatus.PREPARED && this.mPublishStatus != PublishStatus.STARTED?-200:this.mMediaPublisher.stop();
        }
    }

    public synchronized int finishPublishing() {
        LogUtil.d("AlivcVideoChatHostWarn", "finishPublishing.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else if(this.mPublishStatus == PublishStatus.STOPED) {
            this.mPublishStatus = PublishStatus.FINISHED;
            return this.mMediaPublisher.reset();
        } else {
            return -200;
        }
    }

    public void switchCamera() {
        LogUtil.d("AlivcVideoChatHostWarn", "switchCamera.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.switchCamera();
        }

    }

    public void setPublisherMuteModeOn(boolean silent) {
        LogUtil.d("AlivcVideoChatHostWarn", "setPublisherMuteModeOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setSilentOn(silent);
        }

    }

    public void setFlashOn(boolean flashOn) {
        LogUtil.d("AlivcVideoChatHostWarn", "setFlashOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setFlashOn(flashOn);
        }

    }

    public void setAutoFocusOn(boolean autoFocus) {
        LogUtil.d("AlivcVideoChatHostWarn", "setAutoFocusOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setAutoFocusOn(autoFocus);
        }

    }

    public void zoomCamera(float scaleFactor) {
        LogUtil.d("AlivcVideoChatHostWarn", "zoomCamera.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setZoom(scaleFactor);
        }

    }

    public void focusCameraAtAdjustedPoint(float xRatio, float yRatio) {
        LogUtil.d("AlivcVideoChatHostWarn", "focusCameraAtAdjustedPoint.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setFocus(xRatio, yRatio);
        }

    }

    public String getSDKVersion() {
        return "2.1.0.9";
    }

    /** @deprecated */
    @Deprecated
    public void setHostViewScalingMode(VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatHostWarn", "setHostViewScalingMode.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setVideoScalingMode(mode);
        }

    }

    /** @deprecated */
    @Deprecated
    public void setParterViewScalingMode(VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatHostWarn", "setParterViewScalingMode.");
        Iterator i$ = this.mMediaPlayerMap.values().iterator();

        while(i$.hasNext()) {
            AliVcMediaPlayer player = (AliVcMediaPlayer)i$.next();
            if(player != null) {
                if(mode == VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT) {
                    player.setVideoScalingMode(com.alivc.videochat.player.MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                } else {
                    player.setVideoScalingMode(com.alivc.videochat.player.MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }

    }

    public void setScalingMode(VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatHostWarn", "setScalingMode.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setVideoScalingMode(mode);
        }

        Iterator i$ = this.mMediaPlayerMap.values().iterator();

        while(i$.hasNext()) {
            AliVcMediaPlayer player = (AliVcMediaPlayer)i$.next();
            if(player != null) {
                if(mode == VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT) {
                    player.setVideoScalingMode(com.alivc.videochat.player.MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                } else {
                    player.setVideoScalingMode(com.alivc.videochat.player.MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }

    }

    public void setErrorListener(OnErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    public void setInfoListener(OnInfoListener infoListener) {
        this.mInfoListener = infoListener;
    }

    public synchronized int release() {
        LogUtil.d("AlivcVideoChatHostWarn", "release.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setOnPreparedListener((OnPreparedListener)null);
            this.mMediaPublisher.stop();
            this.mMediaPublisher.release();
            this.mMediaPublisher = null;
        }

        Iterator i$ = this.mMediaPlayerMap.values().iterator();

        AliVcMediaPlayer mediaPlayer;
        while(i$.hasNext()) {
            mediaPlayer = (AliVcMediaPlayer)i$.next();
            mediaPlayer.stop();
            mediaPlayer.destroy();
        }

        i$ = this.mFreeMediaPlayerMap.values().iterator();

        while(i$.hasNext()) {
            mediaPlayer = (AliVcMediaPlayer)i$.next();
            mediaPlayer.destroy();
        }

        this.mMediaPlayerMap.clear();
        this.mSurfaceViewMap.clear();
        this.mFreeMediaPlayerMap.clear();
        this.mResumer.release();
        return 0;
    }

    public void setFilterParam(Map<String, String> param) {
        LogUtil.d("AlivcVideoChatHostWarn", "setFilterParam.");
        if(this.mMediaPublisher != null) {
            String value = (String)param.get("alivc_filter_param_beauty_on");
            if(value != null) {
                if(value.equals("true")) {
                    this.mMediaPublisher.setBeautyOn(true);
                } else {
                    this.mMediaPublisher.setBeautyOn(false);
                }
            }
        }

    }

    public AlivcPublisherPerformanceInfo getPublisherPerformanceInfo() {
        LogUtil.d("AlivcVideoChatHost", "getPublisherPerformanceInfo.");
        return this.mMediaPublisher != null?this.mMediaPublisher.getPerformanceInfo():null;
    }

    public AlivcPlayerPerformanceInfo getPlayerPerformanceInfo(String url) {
        LogUtil.d("AlivcVideoChatHost", "getPlayerPerformanceInfo.");
        AliVcMediaPlayer player = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
        AlivcPlayerPerformanceInfo info = new AlivcPlayerPerformanceInfo();
        if(player != null) {
            info.setVideoPacketsInBuffer(player.getPropertyLong(20009, 0L));
            info.setAudioPacketsInBuffer(player.getPropertyLong(20010, 0L));
            info.setVideoDurationFromDownloadToRender(player.getPropertyLong(20011, 0L));
            info.setAudioDurationFromDownloadToRender(player.getPropertyLong(20017, 0L));
            info.setVideoPtsOfLastPacketInBuffer(player.getPropertyLong(20013, 0L));
            info.setAudioPtsOfLastPacketInBuffer(player.getPropertyLong(20014, 0L));
            info.setDowloadSpeed(player.getPropertyLong(20020, 0L) * 8L / 1024L);
            info.setLiveDiscardDuration(player.getPropertyDouble(18011, 0.0D));
        }

        return info;
    }

    public void setPlayerParam(Map<String, String> param) {
        LogUtil.d("AlivcVideoChatHostWarn", "set play param.");
        this.mPlayParam = param;
    }

    public void setPublisherParam(Map<String, String> param) {
        LogUtil.d("AlivcVideoChatHostWarn", "set publish param.");
        this.mPublishParam = param;
        if(this.mPublishParam != null) {
            NativeVideoCallPublisher.getInstance().setPublishParam(MediaConstants.getInt((String)this.mPublishParam.get("UploadTimeout"), 8000), MediaConstants.getInt((String)this.mPublishParam.get("InitBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("MaxBitrate"), 260), MediaConstants.getInt((String)this.mPublishParam.get("MinBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("AudioBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("FrontCameraMirror"), 1));
        }

    }

    public void changeEarPhoneWhenChat(boolean hasEarPhone) {
        LogUtil.d("AlivcVideoChatHost", "change ear phone when chat.");
        NativeVideoCallPublisher.getInstance().setHeadsetOn(hasEarPhone);
    }

    public synchronized int launchChat(String url, SurfaceView parterView) {
        LogUtil.d("AlivcVideoChatHostWarn", "launchChat. " + url);
        this.mPlayUrl = url;
        if(this.mMediaPublisher != null && url != null) {
            if(parterView == null) {
                LogUtil.d("AlivcVideoChatHostWarn", "parter view is null.");
                return -200;
            } else if(this.mChatStatus == ChatStatus.STARTED) {
                LogUtil.d("AlivcVideoChatHostWarn", "chat status is started.");
                return 0;
            } else {
                this.mChatStatus = ChatStatus.STARTED;
                AliVcMediaPlayer.init(this.mContext, "publisher", (AccessKeyCallback)null);
                AliVcMediaPlayer player = null;
                if(this.mFreeMediaPlayerMap.get(parterView) == null) {
                    player = this.createMediaPlayer(this.mContext, parterView);
                } else {
                    player = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(parterView);
                    this.mFreeMediaPlayerMap.remove(parterView);
                }

                this.addMediaPlayerListener(player);
                this.mMediaPublisher.setVideocall(true);
                player.prepareAndPlay(url);
                this.mMediaPlayerMap.put(url, player);
                this.mSurfaceViewMap.put(url, parterView);
                this.mResumer.addPlayerSurfaceHolder(parterView.getHolder());
                VideoPublicPraram.setVideoUrl(url);
                VideoCallStartArgs args = new VideoCallStartArgs();
                args.target_url = url;
                VideoCallStartEvent.sendEvent(args, this.mContext);
                if(this.mInfoListener != null) {
                    this.mInfoListener.onInfo(this, -2001, "");
                }

                LogUtil.d("AlivcVideoChatHostWarn", "launch chat over.");
                return 0;
            }
        } else {
            LogUtil.d("AlivcVideoChatHostWarn", "publish or url is null.");
            return -200;
        }
    }

    /** @deprecated */
    @Deprecated
    public synchronized int reconnectChat() {
        LogUtil.d("AlivcVideoChatHostWarn", "restartToPlayer." + this.mPlayUrl);
        return this.reconnectChat(this.mPlayUrl);
    }

    public synchronized int abortChat() {
        LogUtil.d("AlivcVideoChatHostWarn", "abortChat.");
        if(this.mMediaPublisher == null) {
            LogUtil.d("AlivcVideoChatHostWarn", "publisher is null.");
            return -200;
        } else if(this.mChatStatus == ChatStatus.STARTED) {
            this.mChatStatus = ChatStatus.STOPED;
            this.mMediaPublisher.setVideocall(false);
            Set urls = this.mMediaPlayerMap.keySet();
            Iterator i$ = urls.iterator();

            while(i$.hasNext()) {
                String url = (String)i$.next();
                AliVcMediaPlayer mediaPlayer = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
                if(mediaPlayer == null) {
                    return -200;
                }

                VideoPublicPraram.setUserId(url);
                VideoCallStopEvent.sendEvent(new VideoCallStopArgs(), this.mContext);
                mediaPlayer.stopAndKeepLastFrame();
                this.removeMediaPlayerListener(mediaPlayer);
                this.mFreeMediaPlayerMap.put(this.mSurfaceViewMap.get(url), mediaPlayer);
            }

            this.mMediaPlayerMap.clear();
            this.mSurfaceViewMap.clear();
            this.mResumer.clearPlayerSurface();
            if(this.mInfoListener != null) {
                this.mInfoListener.onInfo(this, -2002, "");
            }

            LogUtil.d("AlivcVideoChatHostWarn", "abortChat over.");
            return 0;
        } else {
            LogUtil.d("AlivcVideoChatHostWarn", "chat status is not started. " + this.mChatStatus);
            return -200;
        }
    }

    public synchronized int pause() {
        LogUtil.d("AlivcVideoChatHostWarn", "pause.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.pause();
        }

        Iterator args = this.mMediaPlayerMap.values().iterator();

        while(args.hasNext()) {
            AliVcMediaPlayer map = (AliVcMediaPlayer)args.next();
            map.releaseVideoSurface();
            map.pause();
        }

        PublisherPauseArgs args1 = new PublisherPauseArgs();
        Map map1 = AlivcMediaPublisher.getPerformanceMap();
        args1.ts = AlivcMediaPublisher.getLong(map1, "mTotalSizeOfUploadedPackets");
        args1.tt = AlivcMediaPublisher.getLong(map1, "mTotalTimeOfPublishing");
        PublisherPauseEvent.sendEvent(args1, this.mContext);
        return 0;
    }

    public synchronized int launchChats(Map<String, SurfaceView> urlSurfaceMap) {
        Log.d("AlivcVideoChatHostWarn", "launchChats.");
        if(urlSurfaceMap != null && urlSurfaceMap.size() != 0) {
            AliVcMediaPlayer.init(this.mContext, "publisher", (AccessKeyCallback)null);
            this.mMediaPublisher.setVideocall(true);
            Iterator i$ = urlSurfaceMap.keySet().iterator();

            while(i$.hasNext()) {
                String url = (String)i$.next();
                LogUtil.d("AlivcVideoChatHostWarn", "launch chat " + url);
                SurfaceView surfaceView = (SurfaceView)urlSurfaceMap.get(url);
                if(surfaceView == null) {
                    return -200;
                }

                AliVcMediaPlayer mediaPlayer = null;
                if(this.mFreeMediaPlayerMap.get(surfaceView) == null) {
                    mediaPlayer = this.createMediaPlayer(this.mContext, surfaceView);
                } else {
                    mediaPlayer = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(surfaceView);
                    this.mFreeMediaPlayerMap.remove(surfaceView);
                }

                this.addMediaPlayerListener(mediaPlayer);
                mediaPlayer.prepareAndPlay(url);
                this.mMediaPlayerMap.put(url, mediaPlayer);
                this.mSurfaceViewMap.put(url, surfaceView);
                this.mResumer.addPlayerSurfaceHolder(surfaceView.getHolder());
                VideoPublicPraram.setVideoUrl(url);
                VideoCallStartArgs args = new VideoCallStartArgs();
                args.target_url = url;
                VideoCallStartEvent.sendEvent(args, this.mContext);
            }

            Log.d("AlivcVideoChatHostWarn", "launchChats over.");
            this.mChatStatus = ChatStatus.STARTED;
            if(this.mInfoListener != null) {
                this.mInfoListener.onInfo(this, -2001, "");
            }

            return 0;
        } else {
            LogUtil.d("AlivcVideoChatHost", "urls or view is invalid.");
            return -200;
        }
    }

    public synchronized int addChats(Map<String, SurfaceView> urlSurfaceMap) {
        LogUtil.d("AlivcVideoChatHostWarn", "add chats.");
        if(urlSurfaceMap != null && urlSurfaceMap.size() != 0) {
            Iterator i$ = urlSurfaceMap.keySet().iterator();

            while(i$.hasNext()) {
                String url = (String)i$.next();
                LogUtil.d("AlivcVideoChatHostWarn", "add chat." + url);
                SurfaceView surfaceView = (SurfaceView)urlSurfaceMap.get(url);
                if(surfaceView == null) {
                    LogUtil.d("AlivcVideoChatHostWarn", "no surface view.");
                    return -200;
                }

                AliVcMediaPlayer mediaPlayer = null;
                if(this.mFreeMediaPlayerMap.get(surfaceView) == null) {
                    mediaPlayer = this.createMediaPlayer(this.mContext, surfaceView);
                } else {
                    mediaPlayer = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(surfaceView);
                    this.mFreeMediaPlayerMap.remove(surfaceView);
                }

                this.addMediaPlayerListener(mediaPlayer);
                LogUtil.d("AlivcVideoChatHostWarn", "prepare and play " + url);
                mediaPlayer.prepareAndPlay(url);
                this.mMediaPlayerMap.put(url, mediaPlayer);
                this.mSurfaceViewMap.put(url, surfaceView);
                this.mResumer.addPlayerSurfaceHolder(surfaceView.getHolder());
            }

            if(this.mInfoListener != null) {
                this.mInfoListener.onInfo(this, -2005, "");
            }

            LogUtil.d("AlivcVideoChatHostWarn", "add chats over.");
            return 0;
        } else {
            LogUtil.d("AlivcVideoChatHost", "urls or view is invalid.");
            return -200;
        }
    }

    public synchronized int removeChats(List<String> urls) {
        LogUtil.d("AlivcVideoChatHostWarn", "remove chats.");
        if(urls != null && urls.size() != 0) {
            Iterator i$ = urls.iterator();

            String url;
            do {
                if(!i$.hasNext()) {
                    i$ = urls.iterator();

                    while(i$.hasNext()) {
                        url = (String)i$.next();
                        LogUtil.d("AlivcVideoChatHostWarn", "remove chat " + url);
                        AliVcMediaPlayer mediaPlayer = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
                        if(mediaPlayer == null) {
                            LogUtil.d("AlivcVideoChatHostWarn", "media player is null.");
                            return -200;
                        }

                        VideoPublicPraram.setUserId(url);
                        VideoCallStopEvent.sendEvent(new VideoCallStopArgs(), this.mContext);
                        mediaPlayer.stopAndKeepLastFrame();
                        this.removeMediaPlayerListener(mediaPlayer);
                        this.mFreeMediaPlayerMap.put(this.mSurfaceViewMap.get(url), mediaPlayer);
                        if(this.mSurfaceViewMap.get(url) != null) {
                            this.mResumer.removePlayerSurfaceHolder(((SurfaceView)this.mSurfaceViewMap.get(url)).getHolder());
                        }

                        this.mMediaPlayerMap.remove(url);
                        this.mSurfaceViewMap.remove(url);
                    }

                    if(this.mInfoListener != null) {
                        this.mInfoListener.onInfo(this, -2006, "");
                    }

                    LogUtil.d("AlivcVideoChatHostWarn", "remove chats over.");
                    return 0;
                }

                url = (String)i$.next();
            } while(this.mMediaPlayerMap.containsKey(url));

            LogUtil.d("AlivcVideoChatHostWarn", "no mediaplay for url ." + url);
            return -200;
        } else {
            LogUtil.d("AlivcVideoChatHost", "url is not valid.");
            return -200;
        }
    }

    private void sleepTwoSeconds() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }

    }

    public synchronized int reconnectChat(final String url) {
        LogUtil.d("AlivcVideoChatHostWarn", "reconnect chat. " + url);
        if(url == null) {
            LogUtil.d("AlivcVideoChatHostWarn", "reconnect chat url is null. ");
            return -200;
        } else {
            final AliVcMediaPlayer player = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
            if(player != null) {
                if(player.isPlaying()) {
                    player.stopAndKeepLastFrame();
                    this.mStopCallbackMap.put(url, new Callback() {
                        public void onEvent() {
                            player.prepareAndPlay(url);
                        }
                    });
                } else {
                    player.stopAndKeepLastFrame();
                    this.sleepTwoSeconds();
                    player.prepareAndPlay(url);
                }
            } else {
                LogUtil.d("AlivcVideoChatHostWarn", "player is null.");
            }

            return 0;
        }
    }

    public synchronized int resume() {
        LogUtil.d("AlivcVideoChatHostWarn", "resume.");
        this.mResumer.resume(this.mMediaPublisher, this.mMediaPlayerMap, this.mSurfaceViewMap);
        PublisherResumeArgs args = new PublisherResumeArgs();
        Map map = AlivcMediaPublisher.getPerformanceMap();
        args.ts = AlivcMediaPublisher.getLong(map, "mTotalSizeOfUploadedPackets");
        args.tt = AlivcMediaPublisher.getLong(map, "mTotalTimeOfPublishing");
        args.cost = System.currentTimeMillis() - PublisherPauseEvent.mLastPauseTime;
        PublisherResumeEvent.sendEvent(args, this.mContext);
        PublisherPauseEvent.mLastPauseTime = -1L;
        return 0;
    }

    private AliVcMediaPlayer createMediaPlayer(Context context, SurfaceView playerView) {
        AliVcMediaPlayer mediaPlayer = new AliVcMediaPlayer(context, playerView, this.mSoundManagerId);
        mediaPlayer.setDefaultDecoder(1);
        if(this.mPlayParam != null) {
            mediaPlayer.setTimeout(MediaConstants.getInt((String)this.mPlayParam.get("PlayerTimeout"), 15000));
            mediaPlayer.setMaxBufferDuration(MediaConstants.getInt((String)this.mPlayParam.get("PlayerMaxBufferDuration"), 1000));
            if(MediaConstants.getBoolean((String)this.mPlayParam.get("PlayerEnableNativeLog"), false)) {
                mediaPlayer.enableNativeLog();
            } else {
                mediaPlayer.disableNativeLog();
            }

            mediaPlayer.setMuteModeOn(MediaConstants.getBoolean((String)this.mPlayParam.get("MuteMode"), false));
        } else {
            mediaPlayer.setMaxBufferDuration(1000);
            mediaPlayer.setTimeout(15000);
            mediaPlayer.disableNativeLog();
        }

        mediaPlayer.setVideoScalingMode(com.alivc.videochat.player.MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        return mediaPlayer;
    }

    private void addMediaPlayerListener(AliVcMediaPlayer mediaPlayer) {
        mediaPlayer.setErrorListener(this.mPlayerErrorListener);
        mediaPlayer.setCompletedListener(this.mMediaPlayerCompletedListener);
        mediaPlayer.setInfoListener(this.mMediaPlayerInfoListener);
        mediaPlayer.setPreparedListener(this.mPlayerPreparedListener);
        mediaPlayer.setStopedListener(this.mMediaPlayerStopedListener);
    }

    private void removeMediaPlayerListener(AliVcMediaPlayer mediaPlayer) {
        mediaPlayer.setErrorListener((MediaPlayerErrorListener)null);
        mediaPlayer.setCompletedListener((MediaPlayerCompletedListener)null);
        mediaPlayer.setInfoListener((MediaPlayerInfoListener)null);
        mediaPlayer.setPreparedListener((MediaPlayerPreparedListener)null);
        mediaPlayer.setStopedListener((MediaPlayerStopedListener)null);
        mediaPlayer.setStartedListener((MediaPlayerStartedListener)null);
    }
}
