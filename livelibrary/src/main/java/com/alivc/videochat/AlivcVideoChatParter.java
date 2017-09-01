//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.content.Context;
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
import com.alivc.videochat.player.MediaPlayer.VideoScalingMode;
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

public class AlivcVideoChatParter implements IVideoChatParter {
    public static final String INFO_TAG = "AlivcVideoChatParterInfo";
    public static final String DEBUG_TAG = "AlivcVideoChatParterDebug";
    public static final String WARN_TAG = "AlivcVideoChatParterWarn";
    private AlivcMediaPublisher mMediaPublisher;
    private AliVcMediaPlayer mHostMediaPlayer;
    private ParterResumer mResumer = new ParterResumer();
    private Context mContext;
    private Map<String, AliVcMediaPlayer> mMediaPlayerMap = new HashMap();
    private Map<String, SurfaceView> mSurfaceViewMap = new HashMap();
    private Map<String, Callback> mStopCallbackMap = new HashMap();
    private Map<SurfaceView, AliVcMediaPlayer> mFreeMediaPlayerMap = new HashMap();
    private OnErrorListener mErrorListener;
    private OnInfoListener mInfoListener;
    private ChatStatus mChatStatus;
    private int mOnlineChatsStatus;
    private int mOfflineChatsStatus;
    private PlayStatus mHostPlayStatus;
    private boolean mHostPlayerStarted;
    private int mSoundManagerId;
    private String mOriginalUrl;
    private String mVideoChatPlayUrl;
    private String mReconnectUrl;
    private Map<String, String> mPlayParam;
    private Map<String, String> mPublishParam;
    private com.alivc.videochat.publisher.IMediaPublisher.OnErrorListener mPublishErrorListener;
    private com.alivc.videochat.publisher.IMediaPublisher.OnInfoListener mPublishInfoListener;
    private MediaPlayerErrorListener mPlayerErrorListener;
    private MediaPlayerPreparedListener mPlayerPreparedListener;
    private MediaPlayerCompletedListener mMediaPlayerCompletedListener;
    private MediaPlayerInfoListener mMediaPlayerInfoListener;
    private MediaPlayerStartedListener mMediaPlayerStartedListener;
    private MediaPlayerStopedListener mMediaPlayerStopedListener;

    public AlivcVideoChatParter() {
        this.mChatStatus = ChatStatus.UNSTARTED;
        this.mOnlineChatsStatus = 0;
        this.mOfflineChatsStatus = 0;
        this.mHostPlayStatus = PlayStatus.UNSTARTED;
        this.mHostPlayerStarted = false;
        this.mSoundManagerId = -1;
        this.mOriginalUrl = null;
        this.mVideoChatPlayUrl = null;
        this.mReconnectUrl = null;
        this.mPlayParam = null;
        this.mPublishParam = null;
        this.mPublishErrorListener = new com.alivc.videochat.publisher.IMediaPublisher.OnErrorListener() {
            public boolean onError(IMediaPublisher mp, int errCode, String extra) {
                LogUtil.d("AlivcVideoChatParterWarn", "publish error: " + errCode);
                if(AlivcVideoChatParter.this.mErrorListener != null) {
                    AlivcVideoChatParter.this.mErrorListener.onError(AlivcVideoChatParter.this, errCode, extra);
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.mPublishInfoListener = new com.alivc.videochat.publisher.IMediaPublisher.OnInfoListener() {
            public boolean onInfo(IMediaPublisher mp, int errCode, String extra) {
                LogUtil.d("AlivcVideoChatParterDebug", "publish info: " + errCode);
                if(AlivcVideoChatParter.this.mInfoListener != null) {
                    AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, errCode, extra);
                }

                return true;
            }
        };
        this.mPlayerErrorListener = new MediaPlayerErrorListener() {
            public void onError(int what, int extra, String url) {
                LogUtil.d("AlivcVideoChatParterWarn", "player error: " + what);
                if(what == 401 || what == 504) {
                    what = 400;
                }

                PublisherErrorArgs args = new PublisherErrorArgs();
                Map map = AlivcMediaPublisher.getPerformanceMap();
                args.ts = AlivcMediaPublisher.getLong(map, "mTotalSizeOfUploadedPackets");
                args.tt = AlivcMediaPublisher.getLong(map, "mTotalTimeOfPublishing");
                args.error_code = (long)what;
                args.error_msg = extra + "_" + url;
                PublisherErrorEvent.sendEvent(args, AlivcVideoChatParter.this.mContext);
                if(AlivcVideoChatParter.this.mErrorListener != null) {
                    AlivcVideoChatParter.this.mErrorListener.onError(AlivcVideoChatParter.this, what, url);
                }

                if(AlivcVideoChatParter.this.mChatStatus == ChatStatus.STARTING) {
                    AlivcVideoChatParter.this.mChatStatus = ChatStatus.STARTED;
                    AlivcVideoChatParter.this.mHostPlayerStarted = true;
                }

            }
        };
        this.mPlayerPreparedListener = new MediaPlayerPreparedListener() {
            public void onPrepared(final String url) {
                LogUtil.d("AlivcVideoChatParterDebug", "player info: 150");
                (new Thread(new Runnable() {
                    public void run() {
                        if(AlivcVideoChatParter.this.mHostMediaPlayer != null) {
                            AlivcVideoChatParter.this.mHostMediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                        }

                        if(AlivcVideoChatParter.this.mHostMediaPlayer != null) {
                            Iterator i$;
                            AliVcMediaPlayer player;
                            if(AlivcVideoChatParter.this.mChatStatus == ChatStatus.STARTED) {
                                AlivcVideoChatParter.this.mHostMediaPlayer.setWorkMode(WorkMode.WORK_AS_VIDEOCHAT);
                                i$ = AlivcVideoChatParter.this.mMediaPlayerMap.values().iterator();

                                while(i$.hasNext()) {
                                    player = (AliVcMediaPlayer)i$.next();
                                    player.setWorkMode(WorkMode.WORK_AS_VIDEOCHAT);
                                }
                            } else {
                                AlivcVideoChatParter.this.mHostMediaPlayer.setWorkMode(WorkMode.WORK_AS_PLAYER);
                                i$ = AlivcVideoChatParter.this.mMediaPlayerMap.values().iterator();

                                while(i$.hasNext()) {
                                    player = (AliVcMediaPlayer)i$.next();
                                    player.setWorkMode(WorkMode.WORK_AS_PLAYER);
                                }
                            }
                        }

                        if(AlivcVideoChatParter.this.mInfoListener != null) {
                            AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, 150, url);
                        }

                    }
                })).start();
                LogUtil.d("AlivcVideoChatParterDebug", "set player started.");
            }
        };
        this.mMediaPlayerCompletedListener = new MediaPlayerCompletedListener() {
            public void onCompleted(String url) {
                LogUtil.d("AlivcVideoChatParterDebug", "error: 151");
                if(AlivcVideoChatParter.this.mInfoListener != null) {
                    AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, 151, url);
                }

            }
        };
        this.mMediaPlayerInfoListener = new MediaPlayerInfoListener() {
            public void onInfo(int i, int i1, String url) {
                LogUtil.d("AlivcVideoChatParterDebug", "player info: " + i);
                if(AlivcVideoChatParter.this.mInfoListener != null) {
                    AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, i, url);
                }

            }
        };
        this.mMediaPlayerStartedListener = new MediaPlayerStartedListener() {
            public void onStarted(String url) {
                if(url.equals(AlivcVideoChatParter.this.mOriginalUrl) || url.equals(AlivcVideoChatParter.this.mVideoChatPlayUrl)) {
                    AlivcVideoChatParter.this.mHostPlayerStarted = true;
                    LogUtil.d("AlivcVideoChatParterInfo", "set player started " + url);
                }

            }
        };
        this.mMediaPlayerStopedListener = new MediaPlayerStopedListener() {
            public void onStopped(String url) {
                LogUtil.d("AlivcVideoChatParterDebug", "player info: 152");
                if(AlivcVideoChatParter.this.mInfoListener != null) {
                    AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, 152, url);
                }

                if(url.equals(AlivcVideoChatParter.this.mOriginalUrl) || url.equals(AlivcVideoChatParter.this.mVideoChatPlayUrl)) {
                    AlivcVideoChatParter.this.mHostPlayerStarted = false;
                    LogUtil.d("AlivcVideoChatParterDebug", "set player stoped " + url);
                }

                if(AlivcVideoChatParter.this.mStopCallbackMap.containsKey(url)) {
                    Callback callback = (Callback)AlivcVideoChatParter.this.mStopCallbackMap.get(url);
                    callback.onEvent();
                    LogUtil.d("AlivcVideoChatParterDebug", "stop callback is called");
                } else {
                    LogUtil.d("AlivcVideoChatParterDebug", "has no stop callback for " + url);
                }

            }
        };
        Random random = new Random();
        this.mMediaPublisher = new AlivcMediaPublisher();
        this.mMediaPublisher.setOnErrorListener(this.mPublishErrorListener);
        this.mMediaPublisher.setOnInfoListener(this.mPublishInfoListener);
        this.mSoundManagerId = random.nextInt(10000);
    }

    public int init(Context context) {
        LogUtil.d("AlivcVideoChatParterInfo", "init.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else if(context == null) {
            return -200;
        } else {
            this.mContext = context;
            int result = this.mMediaPublisher.init(context);
            LogUtil.d("AlivcVideoChatParterDebug", "init over.");
            return result;
        }
    }

    public synchronized int startToPlay(String url, SurfaceView surfaceView) {
        LogUtil.d("AlivcVideoChatParterInfo", "startToPlay. url = " + url);
        if(this.mMediaPublisher != null && url != null) {
            if(surfaceView == null) {
                return -200;
            } else if(this.mHostPlayStatus == PlayStatus.STARTED) {
                return 0;
            } else {
                PublicPraram.setVideoUrl(url);
                this.mHostPlayStatus = PlayStatus.STARTED;
                this.mResumer.setHostPlayerSurfaceHolder(surfaceView.getHolder());
                AliVcMediaPlayer.init(this.mContext, "publisher", (AccessKeyCallback)null);
                if(this.mFreeMediaPlayerMap.get(surfaceView) == null) {
                    this.mHostMediaPlayer = this.createMediaPlayer(this.mContext, surfaceView);
                } else {
                    this.mHostMediaPlayer = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(surfaceView);
                    this.mFreeMediaPlayerMap.remove(surfaceView);
                }

                this.addMediaPlayerListener(this.mHostMediaPlayer);
                if(this.mHostMediaPlayer != null) {
                    this.mHostMediaPlayer.prepareAndPlay(url);
                }

                this.mOriginalUrl = url;
                this.mReconnectUrl = url;
                return 0;
            }
        } else {
            return -200;
        }
    }

    public synchronized int stopPlaying() {
        LogUtil.d("AlivcVideoChatParterInfo", "stopPlaying.");
        if(this.mMediaPublisher == null) {
            return -200;
        } else if(this.mHostPlayStatus == PlayStatus.STARTED) {
            final AliVcMediaPlayer player = this.mHostMediaPlayer;
            (new Thread(new Runnable() {
                public void run() {
                    int count = 0;

                    while((!AlivcVideoChatParter.this.mHostPlayerStarted || AlivcVideoChatParter.this.mChatStatus == ChatStatus.STOPPING) && count++ < 10) {
                        LogUtil.d("AlivcVideoChatParterInfo", "sleep. host player started " + AlivcVideoChatParter.this.mHostPlayerStarted + ", chat status = " + AlivcVideoChatParter.this.mChatStatus + ", i = " + count);

                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }

                    if(player != null) {
                        player.stopAndKeepLastFrame();
                        AlivcVideoChatParter.this.removeMediaPlayerListener(player);
                        AlivcVideoChatParter.this.mResumer.setHostPlayerSurfaceHolder((SurfaceHolder)null);
                        LogUtil.d("AlivcVideoChatParterInfo", "stopPlaying over.");
                    }

                    LogUtil.d("AlivcVideoChatParterInfo", "set player stoped.");
                    AlivcVideoChatParter.this.mHostPlayStatus = PlayStatus.STOPED;
                }
            })).start();
            return 0;
        } else {
            return -200;
        }
    }

    public synchronized int onlineChat(String publisherUrl, int width, int height, SurfaceView previewSurface, Map<String, String> publisherParam, String playUrl) {
        LogUtil.d("AlivcVideoChatParterInfo", "onlineChat.");
        return this.onlineChats(publisherUrl, width, height, previewSurface, publisherParam, playUrl, (Map)null);
    }

    public synchronized int offlineChat() {
        LogUtil.d("AlivcVideoChatParterInfo", "offlineChat.");
        if(this.mChatStatus != ChatStatus.STARTED) {
            LogUtil.d("AlivcVideoChatParterWarn", "offlineChat not started." + this.mChatStatus);
            return -200;
        } else if(this.mMediaPublisher != null && this.mHostMediaPlayer != null) {
            this.mChatStatus = ChatStatus.STOPPING;
            this.mOfflineChatsStatus = 0;
            (new Thread(new Runnable() {
                public void run() {
                    LogUtil.d("AlivcVideoChatParterInfo", "offlineChat. new thread start.");
                    int count = 0;

                    while(!AlivcVideoChatParter.this.mHostPlayerStarted && count++ < 10) {
                        try {
                            Thread.sleep(1000L);
                            LogUtil.d("AlivcVideoChatParterWarn", "sleep 1000 when host player not started and count < 10 when offline chat.");
                        } catch (InterruptedException var5) {
                            var5.printStackTrace();
                        }
                    }

                    if(AlivcVideoChatParter.this.mHostMediaPlayer != null) {
                        AlivcVideoChatParter.this.mHostMediaPlayer.stopAndKeepLastFrame();
                        LogUtil.d("AlivcVideoChatParterInfo", "stop and keep last frame");
                        AlivcVideoChatParter.this.mStopCallbackMap.put(AlivcVideoChatParter.this.mVideoChatPlayUrl, new Callback() {
                            public void onEvent() {
                                AlivcVideoChatParter.this.mHostMediaPlayer.prepareAndPlay(AlivcVideoChatParter.this.mOriginalUrl);
                                LogUtil.d("AlivcVideoChatParterInfo", "prepare and play " + AlivcVideoChatParter.this.mOriginalUrl);
                                AlivcVideoChatParter.this.mOfflineChatsStatus = 1;
                                LogUtil.d("AlivcVideoChatParterInfo", "offline chat status host player started.");
                                AlivcVideoChatParter.this.checkOfflineChatCompleted();
                            }
                        });
                    } else {
                        LogUtil.d("AlivcVideoChatParterWarn", "host meida player is null when offline chat.");
                    }

                    if(AlivcVideoChatParter.this.mMediaPublisher != null) {
                        AlivcVideoChatParter.this.mMediaPublisher.stop();
                        AlivcVideoChatParter.this.mOfflineChatsStatus = 2;
                        LogUtil.d("AlivcVideoChatParterInfo", "stop publish");
                    } else {
                        LogUtil.d("AlivcVideoChatParterWarn", "meida publisher is null when offline chat.");
                    }

                    Iterator i$ = AlivcVideoChatParter.this.mMediaPlayerMap.keySet().iterator();

                    while(i$.hasNext()) {
                        String url = (String)i$.next();
                        LogUtil.d("AlivcVideoChatParterInfo", "stop " + url);
                        AliVcMediaPlayer player = (AliVcMediaPlayer)AlivcVideoChatParter.this.mMediaPlayerMap.get(url);
                        player.stopAndKeepLastFrame();
                        AlivcVideoChatParter.this.removeMediaPlayerListener(player);
                        AlivcVideoChatParter.this.mFreeMediaPlayerMap.put(AlivcVideoChatParter.this.mSurfaceViewMap.get(url), player);
                    }

                    AlivcVideoChatParter.this.mMediaPlayerMap.clear();
                    AlivcVideoChatParter.this.mSurfaceViewMap.clear();
                    AlivcVideoChatParter.this.mResumer.clearPlayerSurfaceView();
                    AlivcVideoChatParter.this.mOfflineChatsStatus = 4;
                    LogUtil.d("AlivcVideoChatParterInfo", "offline chat status players stopped.");
                    AlivcVideoChatParter.this.checkOfflineChatCompleted();
                    LogUtil.d("AlivcVideoChatParterInfo", "offlineChat thread over.");
                }
            })).start();
            return 0;
        } else {
            LogUtil.d("AlivcVideoChatParterWarn", "offlineChat.publisher or player is null.");
            return -200;
        }
    }

    private void checkOfflineChatCompleted() {
        LogUtil.d("AlivcVideoChatParterInfo", "offlineChat check status. " + this.mOfflineChatsStatus);
        if((this.mOfflineChatsStatus & 7) == 7) {
            this.mChatStatus = ChatStatus.STOPED;
            LogUtil.d("AlivcVideoChatParterInfo", "offlineChat over.");
            VideoCallStopArgs args = new VideoCallStopArgs();
            VideoCallStopEvent.sendEvent(args, this.mContext);
            if(this.mInfoListener != null) {
                LogUtil.d("AlivcVideoChatParterInfo", "offlineChat notify offline chat end.");
                this.mInfoListener.onInfo(this, -2004, "");
            } else {
                LogUtil.d("AlivcVideoChatParterInfo", "offlineChat no info listener.");
            }
        }

    }

    public synchronized int reconnect() {
        LogUtil.d("AlivcVideoChatParterInfo", "reconnect");
        (new Thread(new Runnable() {
            public void run() {
                if(AlivcVideoChatParter.this.mHostMediaPlayer != null) {
                    LogUtil.d("AlivcVideoChatParterInfo", "stop and keep last frame.");
                    AlivcVideoChatParter.this.mHostMediaPlayer.stopAndKeepLastFrame();

                    try {
                        LogUtil.d("AlivcVideoChatParterInfo", "sleep 1000");
                        Thread.sleep(1000L);
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    }

                    while(AlivcVideoChatParter.this.mHostPlayerStarted) {
                        try {
                            LogUtil.d("AlivcVideoChatParterInfo", "sleep 1000");
                            Thread.sleep(1000L);
                        } catch (InterruptedException var2) {
                            var2.printStackTrace();
                        }
                    }

                    if(AlivcVideoChatParter.this.mVideoChatPlayUrl != null) {
                        LogUtil.d("AlivcVideoChatParterInfo", "prepare and play " + AlivcVideoChatParter.this.mReconnectUrl);
                        AlivcVideoChatParter.this.mHostPlayerStarted = false;
                        AlivcVideoChatParter.this.mHostMediaPlayer.prepareAndPlay(AlivcVideoChatParter.this.mReconnectUrl);
                    }
                }

            }
        })).start();
        return 0;
    }

    public void switchCamera() {
        LogUtil.d("AlivcVideoChatParterInfo", "switchCamera.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.switchCamera();
        }

        LogUtil.d("AlivcVideoChatParterDebug", "switchCamera over.");
    }

    public String getSDKVersion() {
        return "2.1.0.9";
    }

    /** @deprecated */
    @Deprecated
    public void setParterViewScalingMode(com.alivc.videochat.VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatParterInfo", "setParterViewScalingMode.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setVideoScalingMode(mode);
        }

    }

    /** @deprecated */
    @Deprecated
    public void setHostViewScalingMode(com.alivc.videochat.VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatParterInfo", "setHostViewScalingMode.");
        if(this.mHostMediaPlayer != null) {
            if(mode == com.alivc.videochat.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT) {
                this.mHostMediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            } else {
                this.mHostMediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
        }

    }

    public void setScalingMode(com.alivc.videochat.VideoScalingMode mode) {
        LogUtil.d("AlivcVideoChatParterInfo", "setScalingMode.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setVideoScalingMode(mode);
        }

        if(this.mHostMediaPlayer != null) {
            if(mode == com.alivc.videochat.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT) {
                this.mHostMediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            } else {
                this.mHostMediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
        }

    }

    public synchronized int pause() {
        LogUtil.d("AlivcVideoChatParterInfo", "pause.");
        if(this.mHostMediaPlayer != null) {
            this.mHostMediaPlayer.releaseVideoSurface();
            this.mHostMediaPlayer.pause();
        }

        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.pause();
        }

        Iterator args = this.mMediaPlayerMap.values().iterator();

        while(args.hasNext()) {
            AliVcMediaPlayer map = (AliVcMediaPlayer)args.next();
            if(map != null) {
                map.releaseVideoSurface();
                map.pause();
            }
        }

        PublisherPauseArgs args1 = new PublisherPauseArgs();
        Map map1 = AlivcMediaPublisher.getPerformanceMap();
        args1.ts = AlivcMediaPublisher.getLong(map1, "mTotalSizeOfUploadedPackets");
        args1.tt = AlivcMediaPublisher.getLong(map1, "mTotalTimeOfPublishing");
        PublisherPauseEvent.sendEvent(args1, this.mContext);
        LogUtil.d("AlivcVideoChatParterDebug", "pause over.");
        return 0;
    }

    public synchronized int release() {
        LogUtil.d("AlivcVideoChatParterInfo", "release.");
        final AlivcMediaPublisher mediaPublisher = this.mMediaPublisher;
        this.mMediaPublisher = null;
        (new Thread(new Runnable() {
            public void run() {
                if(mediaPublisher != null) {
                    mediaPublisher.setOnPreparedListener((OnPreparedListener)null);
                    mediaPublisher.stop();
                    mediaPublisher.release();
                }

            }
        })).start();
        if(this.mHostMediaPlayer != null) {
            AliVcMediaPlayer i$ = this.mHostMediaPlayer;
            this.mHostMediaPlayer = null;
            i$.stop();
            i$.destroy();
        }

        Iterator i$1 = this.mMediaPlayerMap.values().iterator();

        AliVcMediaPlayer player;
        while(i$1.hasNext()) {
            player = (AliVcMediaPlayer)i$1.next();
            if(player != null) {
                player.stop();
                player.destroy();
            }
        }

        i$1 = this.mFreeMediaPlayerMap.values().iterator();

        while(i$1.hasNext()) {
            player = (AliVcMediaPlayer)i$1.next();
            player.destroy();
        }

        this.mResumer.release();
        this.mSurfaceViewMap.clear();
        this.mMediaPlayerMap.clear();
        this.mStopCallbackMap.clear();
        LogUtil.d("AlivcVideoChatParterDebug", "release over.");
        return 0;
    }

    public void setPublisherMuteModeOn(boolean silent) {
        LogUtil.d("AlivcVideoChatParterInfo", "setPublisherMuteModeOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setSilentOn(silent);
        }

    }

    public void setFlashOn(boolean flashOn) {
        LogUtil.d("AlivcVideoChatParterInfo", "setFlashOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setFlashOn(flashOn);
        }

    }

    public void setAutoFocusOn(boolean autoFocus) {
        LogUtil.d("AlivcVideoChatParterInfo", "setAutoFocusOn.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setAutoFocusOn(autoFocus);
        }

    }

    public void zoomCamera(float scaleFactor) {
        LogUtil.d("AlivcVideoChatParterInfo", "zoomCamera.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setZoom(scaleFactor);
        }

    }

    public void focusCameraAtAdjustedPoint(float xRatio, float yRatio) {
        LogUtil.d("AlivcVideoChatParterInfo", "focusCameraAtAdjustePoint.");
        if(this.mMediaPublisher != null) {
            this.mMediaPublisher.setFocus(xRatio, yRatio);
        }

    }

    public void setErrorListener(OnErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    public void setInfoListener(OnInfoListener infoListener) {
        this.mInfoListener = infoListener;
    }

    public void setFilterParam(Map<String, String> param) {
        LogUtil.d("AlivcVideoChatParterInfo", "setFilterParam.");
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
        LogUtil.d("AlivcVideoChatParterDebug", "getPublisherPerformanceInfo.");
        return this.mMediaPublisher != null?this.mMediaPublisher.getPerformanceInfo():null;
    }

    public AlivcPlayerPerformanceInfo getPlayerPerformanceInfo(String url) {
        LogUtil.d("AlivcVideoChatParterDebug", "getPlayerPerformanceInfo.");
        AlivcPlayerPerformanceInfo info = new AlivcPlayerPerformanceInfo();
        AliVcMediaPlayer mMediaPlayer = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
        if(mMediaPlayer != null) {
            info.setVideoPacketsInBuffer(mMediaPlayer.getPropertyLong(20009, 0L));
            info.setAudioPacketsInBuffer(mMediaPlayer.getPropertyLong(20010, 0L));
            info.setVideoDurationFromDownloadToRender(mMediaPlayer.getPropertyLong(20011, 0L));
            info.setAudioDurationFromDownloadToRender(mMediaPlayer.getPropertyLong(20017, 0L));
            info.setVideoPtsOfLastPacketInBuffer(mMediaPlayer.getPropertyLong(20013, 0L));
            info.setAudioPtsOfLastPacketInBuffer(mMediaPlayer.getPropertyLong(20014, 0L));
            info.setDowloadSpeed(mMediaPlayer.getPropertyLong(20020, 0L) * 8L / 1024L);
            info.setLiveDiscardDuration(mMediaPlayer.getPropertyDouble(18011, 0.0D));
        }

        return info;
    }

    public void setPlayerParam(Map<String, String> param) {
        this.mPlayParam = param;
    }

    public void setPublisherParam(Map<String, String> param) {
        this.mPublishParam = param;
        if(this.mPublishParam != null) {
            NativeVideoCallPublisher.getInstance().setPublishParam(MediaConstants.getInt((String)this.mPublishParam.get("UploadTimeout"), 8000), MediaConstants.getInt((String)this.mPublishParam.get("InitBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("MaxBitrate"), 260), MediaConstants.getInt((String)this.mPublishParam.get("MinBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("AudioBitrate"), 200), MediaConstants.getInt((String)this.mPublishParam.get("FrontCameraMirror"), 1));
        }

    }

    public void changeEarPhoneWhenChat(boolean hasEarPhone) {
        LogUtil.d("AlivcVideoChatParterInfo", "changeEarPhoneWhenChat.");
        NativeVideoCallPublisher.getInstance().setHeadsetOn(hasEarPhone);
    }

    public synchronized int resume() {
        LogUtil.d("AlivcVideoChatParterDebug", "resume.");
        PublisherResumeArgs args = new PublisherResumeArgs();
        Map map = AlivcMediaPublisher.getPerformanceMap();
        args.ts = AlivcMediaPublisher.getLong(map, "mTotalSizeOfUploadedPackets");
        args.tt = AlivcMediaPublisher.getLong(map, "mTotalTimeOfPublishing");
        args.cost = System.currentTimeMillis() - PublisherPauseEvent.mLastPauseTime;
        PublisherResumeEvent.sendEvent(args, this.mContext);
        PublisherPauseEvent.mLastPauseTime = -1L;
        this.mResumer.resume(this.mMediaPublisher, this.mHostMediaPlayer, this.mMediaPlayerMap, this.mSurfaceViewMap);
        return 0;
    }

    public synchronized int onlineChats(final String publisherUrl, int width, int height, SurfaceView previewSurface, Map<String, String> publisherParam, final String hostPlayUrl, Map<String, SurfaceView> urlSurfaceMap) {
        LogUtil.d("AlivcVideoChatParterInfo", "onlineChats. " + hostPlayUrl);
        if(publisherUrl != null && !publisherUrl.trim().equals("")) {
            if(hostPlayUrl != null && !hostPlayUrl.trim().equals("")) {
                if(previewSurface == null) {
                    LogUtil.d("AlivcVideoChatParterWarn", "preview surface is null when online chat.");
                    return -200;
                } else if(publisherParam != null && urlSurfaceMap != null) {
                    if(this.mChatStatus != ChatStatus.STOPED && this.mChatStatus != ChatStatus.UNSTARTED) {
                        LogUtil.d("AlivcVideoChatParterWarn", "online chat  not started." + this.mChatStatus);
                        return -200;
                    } else {
                        this.mChatStatus = ChatStatus.STARTING;
                        this.mOnlineChatsStatus = 0;
                        this.mVideoChatPlayUrl = hostPlayUrl;
                        this.mReconnectUrl = hostPlayUrl;
                        this.mResumer.setPublishSurfaceHolder(previewSurface.getHolder());
                        this.mHostPlayerStarted = false;
                        if(this.mHostMediaPlayer != null) {
                            this.mHostMediaPlayer.setErrorListener((MediaPlayerErrorListener)null);
                            this.mHostMediaPlayer.stopAndKeepLastFrame();
                            LogUtil.d("AlivcVideoChatParterInfo", "stop and keep last frame ");
                        } else {
                            LogUtil.d("AlivcVideoChatParterWarn", "host player is null when online chat.");
                        }

                        LogUtil.d("AlivcVideoChatParterInfo", "add stop callback for " + this.mOriginalUrl);
                        this.mStopCallbackMap.put(this.mOriginalUrl, new Callback() {
                            public void onEvent() {
                                LogUtil.d("AlivcVideoChatParterInfo", "prepare and play " + hostPlayUrl);
                                if(AlivcVideoChatParter.this.mHostMediaPlayer != null) {
                                    AlivcVideoChatParter.this.mHostMediaPlayer.prepareAndPlay(hostPlayUrl);
                                    AlivcVideoChatParter.this.mHostMediaPlayer.setErrorListener(AlivcVideoChatParter.this.mPlayerErrorListener);
                                } else {
                                    LogUtil.d("AlivcVideoChatParterWarn", "host player is null when online chat.");
                                }

                                AlivcVideoChatParter.this.mOnlineChatsStatus = 1;
                                LogUtil.d("AlivcVideoChatParterInfo", "online chat status host player started.");
                                AlivcVideoChatParter.this.checkOnlineChatCompleted();
                            }
                        });
                        this.mMediaPublisher.setOnPreparedListener(new OnPreparedListener() {
                            public void onPrepared() {
                                AlivcVideoChatParter.this.mMediaPublisher.setVideocall(true);
                                LogUtil.d("AlivcVideoChatParterInfo", "start to publish.");
                                VideoPublicPraram.setVideoUrl(publisherUrl);
                                VideoCallStartArgs args = new VideoCallStartArgs();
                                args.target_url = publisherUrl;
                                VideoCallStartEvent.sendEvent(args, AlivcVideoChatParter.this.mContext);
                                int result = AlivcVideoChatParter.this.mMediaPublisher.start(publisherUrl);
                                if(result == 0 && AlivcVideoChatParter.this.mInfoListener != null) {
                                    AlivcVideoChatParter.this.mInfoListener.onInfo(AlivcVideoChatParter.this, -505, publisherUrl);
                                }

                                AlivcVideoChatParter.this.mOnlineChatsStatus = 2;
                                LogUtil.d("AlivcVideoChatParterInfo", "online chat status publish started.");
                                AlivcVideoChatParter.this.checkOnlineChatCompleted();
                            }
                        });
                        LogUtil.d("AlivcVideoChatParterInfo", "prepare to publish.");
                        this.mMediaPublisher.prepare(previewSurface.getHolder().getSurface(), width, height, publisherParam);
                        if(urlSurfaceMap != null && urlSurfaceMap.size() > 0) {
                            Iterator i$ = urlSurfaceMap.keySet().iterator();

                            while(i$.hasNext()) {
                                String url = (String)i$.next();
                                LogUtil.d("AlivcVideoChatParterInfo", "url : " + url);
                                SurfaceView surfaceView = (SurfaceView)urlSurfaceMap.get(url);
                                if(surfaceView != null) {
                                    AliVcMediaPlayer mediaPlayer = null;
                                    if(this.mFreeMediaPlayerMap.get(surfaceView) == null) {
                                        mediaPlayer = this.createMediaPlayer(this.mContext, surfaceView);
                                    } else {
                                        mediaPlayer = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(surfaceView);
                                        this.mFreeMediaPlayerMap.remove(surfaceView);
                                    }

                                    this.addMediaPlayerListener(mediaPlayer);
                                    LogUtil.d("AlivcVideoChatParterInfo", "prepare and play " + url);
                                    mediaPlayer.prepareAndPlay(url);
                                    this.mMediaPlayerMap.put(url, mediaPlayer);
                                    this.mSurfaceViewMap.put(url, surfaceView);
                                    this.mResumer.addPlayerSurfaceHolder(surfaceView.getHolder());
                                }
                            }
                        }

                        this.mOnlineChatsStatus |= 4;
                        LogUtil.d("AlivcVideoChatParterInfo", "online chat status players started.");
                        this.checkOnlineChatCompleted();
                        return 0;
                    }
                } else {
                    LogUtil.d("AlivcVideoChatParterWarn", "publish param or url surface map is null or empty when online chat.");
                    return -200;
                }
            } else {
                LogUtil.d("AlivcVideoChatParterWarn", "host play url is null or empty when online chat.");
                return -200;
            }
        } else {
            LogUtil.d("AlivcVideoChatParterWarn", "publish is null or empty when online chat.");
            return -200;
        }
    }

    private void checkOnlineChatCompleted() {
        if((this.mOnlineChatsStatus & 7) == 7) {
            this.mChatStatus = ChatStatus.STARTED;
            this.mOnlineChatsStatus = 0;
            if(this.mInfoListener != null) {
                this.mInfoListener.onInfo(this, -2003, "");
            }
        }

    }

    public synchronized int addChats(Map<String, SurfaceView> urlSurfaceMap) {
        LogUtil.d("AlivcVideoChatParterWarn", "addChats.");
        if(this.mChatStatus != ChatStatus.STARTED) {
            LogUtil.d("AlivcVideoChatParterWarn", "add chat  not started." + this.mChatStatus);
            return -200;
        } else if(urlSurfaceMap != null && urlSurfaceMap.size() != 0) {
            this.mChatStatus = ChatStatus.ADDING;
            Iterator i$ = urlSurfaceMap.keySet().iterator();

            while(i$.hasNext()) {
                String url = (String)i$.next();
                LogUtil.d("AlivcVideoChatParterWarn", "add chat for " + url);
                SurfaceView surfaceView = (SurfaceView)urlSurfaceMap.get(url);
                if(surfaceView != null) {
                    AliVcMediaPlayer mediaPlayer = null;
                    if(this.mFreeMediaPlayerMap.get(surfaceView) == null) {
                        mediaPlayer = this.createMediaPlayer(this.mContext, surfaceView);
                    } else {
                        mediaPlayer = (AliVcMediaPlayer)this.mFreeMediaPlayerMap.get(surfaceView);
                        this.mFreeMediaPlayerMap.remove(surfaceView);
                    }

                    LogUtil.d("AlivcVideoChatParterWarn", "prepare and play " + url);
                    mediaPlayer.prepareAndPlay(url);
                    this.mMediaPlayerMap.put(url, mediaPlayer);
                    this.mSurfaceViewMap.put(url, surfaceView);
                    this.mResumer.addPlayerSurfaceHolder(surfaceView.getHolder());
                } else {
                    LogUtil.d("AlivcVideoChatParterWarn", "surface view is null when add chat for " + url);
                }
            }

            this.mChatStatus = ChatStatus.STARTED;
            if(this.mInfoListener != null) {
                this.mInfoListener.onInfo(this, -2005, "");
            }

            return 0;
        } else {
            LogUtil.d("AlivcVideoChatParterWarn", "add chat url surface map is null or empty .");
            return -200;
        }
    }

    public synchronized int removeChats(List<String> urls) {
        LogUtil.d("AlivcVideoChatParterInfo", "removeChats.");
        if(urls != null && urls.size() != 0) {
            if(this.mChatStatus != ChatStatus.STARTED) {
                LogUtil.d("AlivcVideoChatParterWarn", "add chat url surface map is null or empty .");
                return -200;
            } else {
                this.mChatStatus = ChatStatus.REMOVING;
                Iterator i$ = urls.iterator();

                while(i$.hasNext()) {
                    String url = (String)i$.next();
                    AliVcMediaPlayer mediaPlayer = (AliVcMediaPlayer)this.mMediaPlayerMap.get(url);
                    SurfaceView surfaceView = (SurfaceView)this.mSurfaceViewMap.get(url);
                    if(mediaPlayer == null) {
                        LogUtil.d("AlivcVideoChatParterInfo", "media player is null.");
                        this.mChatStatus = ChatStatus.STARTED;
                        return -200;
                    }

                    LogUtil.d("AlivcVideoChatParterInfo", "stop player " + url);
                    mediaPlayer.stopAndKeepLastFrame();
                    this.removeMediaPlayerListener(mediaPlayer);
                    this.mFreeMediaPlayerMap.put(surfaceView, mediaPlayer);
                    this.mMediaPlayerMap.remove(url);
                    if(surfaceView == null) {
                        LogUtil.d("AlivcVideoChatParterInfo", "surface view is null.");
                        this.mChatStatus = ChatStatus.STARTED;
                        return -200;
                    }

                    this.mResumer.removePlayerSurfaceHolder(surfaceView.getHolder());
                    this.mSurfaceViewMap.remove(url);
                }

                this.mChatStatus = ChatStatus.STARTED;
                if(this.mInfoListener != null) {
                    this.mInfoListener.onInfo(this, -2006, "");
                }

                return 0;
            }
        } else {
            LogUtil.d("AlivcVideoChatParterWarn", "add chat url surface map is null or empty .");
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

    public synchronized int reconnect(final String url) {
        LogUtil.d("AlivcVideoChatParterInfo", "reconnect." + url);
        if(url == null) {
            return -200;
        } else {
            if(!url.equals(this.mOriginalUrl) && !url.equals(this.mVideoChatPlayUrl)) {
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
                }
            } else if(this.mHostMediaPlayer != null) {
                if(this.mHostMediaPlayer.isPlaying()) {
                    this.mHostMediaPlayer.stopAndKeepLastFrame();
                    this.mStopCallbackMap.put(url, new Callback() {
                        public void onEvent() {
                            AlivcVideoChatParter.this.mHostMediaPlayer.prepareAndPlay(url);
                        }
                    });
                } else {
                    this.mHostMediaPlayer.stopAndKeepLastFrame();
                    this.sleepTwoSeconds();
                    this.mHostMediaPlayer.prepareAndPlay(url);
                }
            }

            return 0;
        }
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

        mediaPlayer.setVideoScalingMode(VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        return mediaPlayer;
    }

    private void addMediaPlayerListener(AliVcMediaPlayer mediaPlayer) {
        mediaPlayer.setErrorListener(this.mPlayerErrorListener);
        mediaPlayer.setCompletedListener(this.mMediaPlayerCompletedListener);
        mediaPlayer.setInfoListener(this.mMediaPlayerInfoListener);
        mediaPlayer.setPreparedListener(this.mPlayerPreparedListener);
        mediaPlayer.setStopedListener(this.mMediaPlayerStopedListener);
        mediaPlayer.setStartedListener(this.mMediaPlayerStartedListener);
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
