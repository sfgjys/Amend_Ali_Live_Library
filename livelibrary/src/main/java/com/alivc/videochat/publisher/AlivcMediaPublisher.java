//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import com.alivc.videochat.PhoneAdapterUtil;
import com.alivc.videochat.VideoScalingMode;
import com.alivc.videochat.logreport.PublicPraram;
import com.alivc.videochat.logreport.PublisherBitrateEvent;
import com.alivc.videochat.logreport.PublisherBitrateEvent.PublisherBitrateArgs;
import com.alivc.videochat.logreport.PublisherDelayEvent;
import com.alivc.videochat.logreport.PublisherDelayEvent.PublisherDelayArgs;
import com.alivc.videochat.logreport.PublisherFpsEvent;
import com.alivc.videochat.logreport.PublisherFpsEvent.PublisherFpsBeatArgs;
import com.alivc.videochat.logreport.PublisherHeartBeatEvent;
import com.alivc.videochat.logreport.PublisherHeartBeatEvent.PublisherHeartBeatArgs;
import com.alivc.videochat.logreport.PublisherPtsEvent;
import com.alivc.videochat.logreport.PublisherPtsEvent.PublisherPtsArgs;
import com.alivc.videochat.logreport.PublisherStartEvent;
import com.alivc.videochat.logreport.PublisherStartEvent.PublisherStartArgs;
import com.alivc.videochat.logreport.PublisherStopEvent;
import com.alivc.videochat.logreport.PublisherStopEvent.PublisherStopArgs;
import com.alivc.videochat.publisher.AudioPusher.AudioSourceListener;
import com.alivc.videochat.publisher.MediaConstants.CameraFacing;
import com.alivc.videochat.publisher.NativeVideoCallPublisher.NotificationListener;
import com.alivc.videochat.publisher.VideoPusher.VideoSourceListener;
import com.alivc.videochat.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlivcMediaPublisher implements IMediaPublisher {
    public static final String TAG = "AlivcMediaPublisher";
    public static final String TAG_WARN = "AlivcMediaPublisherWarn";
    public static final String VERSION = "2.1.0.9";
    private static long sStartTime;
    private final int NTP_TIME_OUT_MILLISECOND = 1000;
    private AudioPusher mAudioSource = null;
    private VideoPusher mVideoPusher = null;
    private Map<String, String> mMediaParam = null;
    private PublishStatus mStatus;
    private boolean isPaused;
    private Context mContext;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private int mCameraId;
    private VideoParam mVideoParam;
    private boolean mFirstFrameRendered;
    private long mTimeDelta;
    private Handler mHandler;
    private NotificationListener mNotificationListener;
    private ScheduledExecutorService executor30;
    private ScheduledExecutorService executor5;

    public AlivcMediaPublisher() {
        this.mStatus = PublishStatus.UINITED;
        this.isPaused = false;
        this.mContext = null;
        this.mOnErrorListener = null;
        this.mOnInfoListener = null;
        this.mCameraId = 1;
        this.mFirstFrameRendered = false;
        this.mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                LogUtil.d("", "notify erro in handle msg : " + msg.what);
                if (msg.what != 410 || AlivcMediaPublisher.this.mStatus != PublishStatus.PREPARED) {
                    int what = msg.what;
                    int arg0 = msg.arg1;
                    int arg1 = msg.arg2;
                    if (AlivcMediaPublisher.this.mOnErrorListener != null && what < 500) {
                        LogUtil.d("", "notify erro in err listen notification r: " + what);
                        AlivcMediaPublisher.this.mOnErrorListener.onError(AlivcMediaPublisher.this, -what, "");
                    } else if (AlivcMediaPublisher.this.mOnInfoListener != null && what >= 500) {
                        AlivcMediaPublisher.this.mOnInfoListener.onInfo(AlivcMediaPublisher.this, -what, "");
                    }

                    super.handleMessage(msg);
                }
            }
        };
        this.mNotificationListener = new NotificationListener() {
            public void onNotification(int what, int arg0, int arg1, int objId) {
                LogUtil.d("", "notify erro in on notification listener: " + what);
                if (what == 504) {
                    AlivcMediaPublisher.this.mFirstFrameRendered = true;
                }

                Message msg = AlivcMediaPublisher.this.mHandler.obtainMessage(what, arg0, arg1, Integer.valueOf(objId));
                AlivcMediaPublisher.this.mHandler.sendMessage(msg);
            }
        };
        this.executor30 = Executors.newSingleThreadScheduledExecutor();
        this.executor5 = Executors.newSingleThreadScheduledExecutor();
    }

    public int init(Context context) {
        if (context == null) {
            return -200;
        } else {
            this.mContext = context;
            this.mStatus = PublishStatus.INITED;
            (new Thread(new Runnable() {
                public void run() {
                    AlivcMediaPublisher.this.mTimeDelta = AlivcMediaPublisher.this.getTimeDelta();
                }
            })).start();
            return 0;
        }
    }

    public int prepare(Surface surface, int width, int height, Map<String, String> mediaParam) {
        if (MediaConstants.getString((String) mediaParam.get("CameraPosition"), MediaConstants.DEFAULT_VALUE_STRING_CAMERA_FACING).equals(CameraFacing.CAMERA_FACING_FRONT.name())) {
            this.mCameraId = 1;
        } else {
            this.mCameraId = 0;
        }

        this.mMediaParam = mediaParam;
        NativeVideoCallPublisher.getInstance().setPreviewSurface(surface);
        int result = this.prepare0(width, height, mediaParam);
        return result;
    }

    private long getTimeDelta() {
        long start = 0L;
        long time = -1L;
        long end = 0L;
        long delta = 0L;

        while (time <= 0L) {
            start = System.currentTimeMillis();
            time = this.getTimeFromNtpServer("time.pool.aliyun.com");
            end = System.currentTimeMillis();
            LogUtil.d("AlivcMediaPublisher", "start = " + start + ", end = " + end + ", time = " + time);
            if (end - start > 100L) {
                time = -1L;
                LogUtil.d("AlivcMediaPublisher", "time is not valid");
            }
        }

        return time - (start + end) / 2L;
    }

    public int start(final String url) {
        if (url != null && !url.equals("")) {
            (new Thread(new Runnable() {
                public void run() {
                    NativeVideoCallPublisher.getInstance().startPublisher(url);
                    PublicPraram.changeRequestId();
                    PublicPraram.setVideoUrl(url);
                    PublisherStartArgs args = new PublisherStartArgs();
                    Map map = AlivcMediaPublisher.getPerformanceMap();
                    args.vutMs = (long) AlivcMediaPublisher.getInteger(map, "mVideoDurationFromeCaptureToUpload");
                    args.autMs = (long) AlivcMediaPublisher.getInteger(map, "mAudioDurationFromeCaptureToUpload");
                    PublisherStartEvent.sendEvent(args, AlivcMediaPublisher.this.mContext);
                    AlivcMediaPublisher.this.start30Interval();
                    AlivcMediaPublisher.this.start5Interval();
                    AlivcMediaPublisher.this.mStatus = PublishStatus.PUBLISH_STARTED;
                }
            })).start();
            return 0;
        } else {
            LogUtil.d("AlivcMediaPublisher", "start url is invalid.");
            return -200;
        }
    }

    public int stop() {
        LogUtil.d("AlivcMediaPublisher", "stop.");
        NativeVideoCallPublisher.getInstance();
        NativeVideoCallPublisher.setNotificationListener((NotificationListener) null);
        if (this.mStatus == PublishStatus.PUBLISH_STARTED || this.mStatus == PublishStatus.PREPARED) {
            NativeVideoCallPublisher.getInstance().stopPublisher();
            PublisherStopArgs args = new PublisherStopArgs();
            Map map = getPerformanceMap();
            args.tus = getLong(map, "mTotalSizeOfUploadedPackets");
            args.tut = getLong(map, "mTotalTimeOfPublishing");
            PublisherStopEvent.sendEvent(args, this.mContext);
            this.stop30Interval();
            this.stop5Interval();
            PublicPraram.setVideoUrl((String) null);
        }

        if (this.mAudioSource != null) {
            this.mAudioSource.stop();
        }

        if (this.mVideoPusher != null) {
            this.mVideoPusher.stopPreview();
        }

        this.mStatus = PublishStatus.PUBLISH_STOPED;
        this.mHandler.removeMessages(0);
        this.mFirstFrameRendered = false;
        return 0;
    }

    public int reset() {
        this.stop();
        this.mStatus = PublishStatus.INITED;
        return 0;
    }

    public int release() {
        NativeVideoCallPublisher.getInstance().releasePublisher();
        this.mStatus = PublishStatus.RELEASED;
        return 0;
    }

    public void switchCamera() {
        if (this.mVideoPusher != null) {
            try {
                this.mVideoPusher.switchCamera();
            } catch (Throwable var2) {
                this.mHandler.sendEmptyMessage(402);
            }

            if (this.mCameraId == 1) {
                this.mCameraId = 0;
            } else {
                this.mCameraId = 1;
            }
        }

    }

    public void setBeautyOn(boolean beauty) {
        NativeVideoCallPublisher.getInstance().setBeautyOn(beauty);
    }

    public void setSilentOn(boolean silent) {
        this.mAudioSource.setMute(silent);
    }

    public void setBlackScreenOn(boolean blackScreen) {
        if (blackScreen) {
            this.mVideoPusher.pausePreview(true);
        } else {
            this.mVideoPusher.resumePreview();
        }

    }

    public void setFlashOn(boolean flashOn) {
        this.mVideoPusher.setFlashOn(flashOn);
    }

    public void setAutoFocusOn(boolean autoFocus) {
        this.mVideoPusher.setAutoFocus(autoFocus);
    }

    public void setBlurOn(boolean blur) {
    }

    public void setZoom(float scaleFactor) {
        this.mVideoPusher.setZoom(scaleFactor);
    }

    public void setFilterParam(Map map) {
    }

    public void setFocus(float xRatio, float yRatio) {
        this.mVideoPusher.setFocus(xRatio, yRatio);
    }

    public String getVersion() {
        return "2.1.0.9";
    }

    public void enablePerformanceInfo() {
    }

    public void disablePerformanceInfo() {
    }

    public static int getInteger(Map<String, String> map, String key) {
        if (map != null && key != null && map.containsKey(key)) {
            String value = (String) map.get(key);

            try {
                int t = Integer.parseInt(value);
                return t;
            } catch (Throwable var4) {
                var4.printStackTrace();
            }
        }

        return 0;
    }

    public static long getLong(Map<String, String> map, String key) {
        if (map != null && key != null && map.containsKey(key)) {
            String value = (String) map.get(key);

            try {
                long t = Long.parseLong(value);
                return t;
            } catch (Throwable var5) {
                var5.printStackTrace();
            }
        }

        return 0L;
    }

    public static Map<String, String> getPerformanceMap() {
        String mapStr = NativeVideoCallPublisher.getInstance().getPerformanceInfo();
        HashMap map = new HashMap();
        StringTokenizer entries = new StringTokenizer(mapStr, "|");

        while (entries.hasMoreTokens()) {
            StringTokenizer items = new StringTokenizer(entries.nextToken(), ":");
            map.put(items.nextToken(), items.hasMoreTokens() ? items.nextToken() : null);
        }

        return map;
    }

    public AlivcPublisherPerformanceInfo getPerformanceInfo() {
        String mapStr = NativeVideoCallPublisher.getInstance().getPerformanceInfo();
        HashMap map = new HashMap();
        StringTokenizer info = new StringTokenizer(mapStr, "|");

        while (info.hasMoreTokens()) {
            StringTokenizer items = new StringTokenizer(info.nextToken(), ":");
            map.put(items.nextToken(), items.hasMoreTokens() ? items.nextToken() : null);
        }

        AlivcPublisherPerformanceInfo info1 = new AlivcPublisherPerformanceInfo();
        info1.setAudioEncodeBitrate(getInteger(map, "mAudioEncodeBitrate"));
        info1.setVideoEncodeBitrate(getInteger(map, "mVideoEncodeBitrate"));
        info1.setAudioUploadBitrate(getInteger(map, "mAudioUploadBitrate"));
        info1.setVideoUploadBitrate(getInteger(map, "mVideoUploadBitrate"));
        info1.setAudioPacketsInBuffer(getInteger(map, "mAudioPacketsInBuffer"));
        info1.setVideoPacketsInBuffer(getInteger(map, "mVideoPacketsInBuffer"));
        info1.setVideoEncodedFps(getInteger(map, "mVideoEncodedFps"));
        info1.setVideoUploadedFps(getInteger(map, "mVideoUploadedFps"));
        info1.setVideoCaptureFps(getInteger(map, "mVideoCaptureFps"));
        info1.setCurrentlyUploadedVideoFramePts(getLong(map, "mCurrentlyUploadedVideoFramePts"));
        info1.setCurrentlyUploadedAudioFramePts(getLong(map, "mCurrentlyUploadedAudioFramePts"));
        info1.setPreviousKeyFramePts(getLong(map, "mPreviousKeyFramePts"));
        info1.setTotalFramesOfEncodedVideo(getLong(map, "mTotalFramesOfEncodedVideo"));
        info1.setTotalTimeOfEncodedVideo(getLong(map, "mTotalTimeOfEncodedVideo"));
        info1.setTotalSizeOfUploadedPackets(getLong(map, "mTotalSizeOfUploadedPackets"));
        info1.setTotalTimeOfPublishing(getLong(map, "mTotalTimeOfPublishing"));
        info1.setTotalFramesOfVideoUploaded(getLong(map, "mTotalFramesOfVideoUploaded"));
        info1.setDropDurationOfVideoFrames(getLong(map, "mDropDurationOfVideoFrames"));
        info1.setVideoDurationFromeCaptureToUpload(getInteger(map, "mVideoDurationFromeCaptureToUpload"));
        info1.setAudioDurationFromeCaptureToUpload(getInteger(map, "mAudioDurationFromeCaptureToUpload"));
        info1.setVideoEncodeBitrate(getInteger(map, "mVideoEncodeBitrate"));
        if (this.mVideoPusher != null) {
            info1.setVideoCaptureFps(this.mVideoPusher.getCurrentFps());
        }

        return info1;
    }

    private int prepare0(int width, int height, Map<String, String> mediaParam) {
        NativeVideoCallPublisher.getInstance();
        NativeVideoCallPublisher.setNotificationListener(this.mNotificationListener);
        this.mAudioSource = new AudioPusher(this.mContext);
        this.mAudioSource.setRecordParams(12, MediaConstants.getInt((String) this.mMediaParam.get("AudioSampleRate"), 32000), MediaConstants.getInt((String) this.mMediaParam.get("AudioFrameSize"), 2972));
        this.mAudioSource.setAudioSourceListener(new AudioSourceListener() {
            public void onAudioFrame(byte[] audioFrame, int length) {
                long time = System.currentTimeMillis();
                AlivcMediaPublisher.this.handleAudioFrame(audioFrame, length, (time + AlivcMediaPublisher.this.mTimeDelta) / 1L);
            }
        });

        try {
            this.mAudioSource.start();
            Thread.sleep(500L);
        } catch (Exception var6) {
            this.mHandler.sendEmptyMessage(405);
        }

        this.mVideoParam = new VideoParam(1280, 720, MediaConstants.getInt((String) this.mMediaParam.get("InitBitrate"), 200), MediaConstants.getInt((String) this.mMediaParam.get("VideoFps"), 25), this.mCameraId, MediaConstants.getInt((String) mediaParam.get("Rotation"), 0));
        this.mVideoParam.setPushWidth(width > height ? width : height);
        this.mVideoParam.setPushHeight(width > height ? height : width);
        this.mVideoPusher = new VideoPusher(this.mContext, this.mHandler, this.mVideoParam);
        this.mVideoPusher.setVideoSourceListener(new VideoSourceListener() {
            public void onVideoFrame(byte[] videoFrame, int oritation) {
                AlivcMediaPublisher.this.handleVideoFrame(videoFrame, (System.currentTimeMillis() + AlivcMediaPublisher.this.mTimeDelta) / 1L, oritation);
            }
        });

        try {
            this.mVideoPusher.startPreview();
        } catch (Throwable var5) {
            this.mHandler.sendEmptyMessage(402);
            return -402;
        }

        this.mStatus = PublishStatus.PREPARED;
        return 0;
    }

    private int handleVideoFrame(byte[] frame, long timestamp, int orientation) {
        if (this.mStatus == PublishStatus.PUBLISH_STARTED || this.mStatus == PublishStatus.PREPARED) {
            long start = System.currentTimeMillis();
            NativeVideoCallPublisher.getInstance().handleVideoFrame(frame, timestamp, this.mCameraId, orientation);
            Log.v("AlivcMediaPublisherWarn", "handle video frame. pts = " + (timestamp & 2147483647L));
        }

        return 0;
    }

    private int handleAudioFrame(byte[] frame, int length, long timestamp) {
        if (this.mStatus == PublishStatus.PUBLISH_STARTED || this.mStatus == PublishStatus.PREPARED) {
            NativeVideoCallPublisher.getInstance().handleAudioFrame(frame, length, timestamp);
            Log.v("AlivcMediaPublisherWarn", "handle audio frame. pts = " + (timestamp & 2147483647L));
        }

        return 0;
    }

    public void setVideoScalingMode(VideoScalingMode mode) {
        NativeVideoCallPublisher.getInstance().setPreviewScallingMode(mode.ordinal());
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
    }

    public void pause() {
        if (this.mStatus == PublishStatus.PREPARED || this.mStatus == PublishStatus.PUBLISH_STARTED) {
            this.isPaused = true;
            NativeVideoCallPublisher.getInstance().pausePublisher();
            if (this.mVideoPusher != null) {
                this.mVideoPusher.pausePreview(false);
            }

            if (this.mAudioSource != null) {
                this.mAudioSource.pause();
            }
        }

    }

    public void resume(Surface previewSurface) {
        if ((this.mStatus == PublishStatus.PUBLISH_STARTED || this.mStatus == PublishStatus.PREPARED) && !PhoneAdapterUtil.isHwNexus6P()) {
            NativeVideoCallPublisher.getInstance().resumePublisher(previewSurface);
        }

        if (this.mStatus == PublishStatus.PUBLISH_STARTED || this.mStatus == PublishStatus.PREPARED) {
            if (this.mVideoPusher != null) {
                this.mVideoPusher.resumePreview();
            }

            if (this.mAudioSource != null) {
                this.mAudioSource.resume();
            }
        }

        this.isPaused = false;
    }

    public void resetVideoPusher() {
        this.mVideoPusher.reset();
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        VideoPusher.setOnPreparedListener(onPreparedListener);
    }

    public void setVideocall(boolean videocall) {
        NativeVideoCallPublisher.getInstance().setVideocall(videocall);
    }

    private long getTimeFromNtpServer(String ntpHost) {
        LogUtil.d("", "get time from " + ntpHost);
        AlivcMediaPublisher.SntpClient client = new AlivcMediaPublisher.SntpClient();
        boolean isSuccessful = client.requestTime(ntpHost, 1000);
        return isSuccessful ? client.getNtpTime() : -1L;
    }

    private void start5Interval() {
        if (this.executor5 == null || this.executor5.isShutdown()) {
            this.executor5 = Executors.newSingleThreadScheduledExecutor();
        }

        this.executor5.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Map map = AlivcMediaPublisher.getPerformanceMap();
                PublisherFpsBeatArgs args = new PublisherFpsBeatArgs();
                args.vef = (long) AlivcMediaPublisher.getInteger(map, "mVideoEncodedFps");
                args.vuf = (long) AlivcMediaPublisher.getInteger(map, "mVideoUploadedFps");
                args.vcf = (long) AlivcMediaPublisher.getInteger(map, "mVideoCaptureFps");
                args.tf = (long) AlivcMediaPublisher.getInteger(map, "mTotalFramesOfVideoUploaded");
                args.df = (long) AlivcMediaPublisher.getInteger(map, "mDropDurationOfVideoFrames");
                args.abf = (long) AlivcMediaPublisher.getInteger(map, "mAudioPacketsInBuffer");
                args.vbfs = (long) AlivcMediaPublisher.getInteger(map, "mVideoPacketsInBuffer");
                PublisherFpsEvent.sendEvent(args, AlivcMediaPublisher.this.mContext);
                PublisherBitrateArgs args1 = new PublisherBitrateArgs();
                args1.aeb = (long) AlivcMediaPublisher.getInteger(map, "mAudioEncodeBitrate");
                args1.veb = (long) AlivcMediaPublisher.getInteger(map, "mVideoEncodeBitrate");
                args1.aub = (long) AlivcMediaPublisher.getInteger(map, "mAudioUploadBitrate");
                args1.vub = (long) AlivcMediaPublisher.getInteger(map, "mVideoUploadBitrate");
                args1.vepb = (long) AlivcMediaPublisher.getInteger(map, "mVideoEncodeBitrate");
                PublisherBitrateEvent.sendEvent(args1, AlivcMediaPublisher.this.mContext);
                PublisherDelayArgs args2 = new PublisherDelayArgs();
                args2.aut = (long) AlivcMediaPublisher.getInteger(map, "mAudioDurationFromeCaptureToUpload");
                args2.vut = (long) AlivcMediaPublisher.getInteger(map, "mVideoDurationFromeCaptureToUpload");
                PublisherDelayEvent.sendEvent(args2, AlivcMediaPublisher.this.mContext);
                PublisherPtsArgs args3 = new PublisherPtsArgs();
                args3.vpts = AlivcMediaPublisher.getLong(map, "mCurrentlyUploadedVideoFramePts");
                args3.apts = AlivcMediaPublisher.getLong(map, "mCurrentlyUploadedAudioFramePts");
                args3.vbpts = AlivcMediaPublisher.getLong(map, "mPreviousKeyFramePts");
                args3.abpts = AlivcMediaPublisher.getLong(map, "mPreviousKeyFramePts");
                PublisherPtsEvent.sendEvent(args3, AlivcMediaPublisher.this.mContext);
            }
        }, 0L, 5000L, TimeUnit.MILLISECONDS);
    }

    private void start30Interval() {
        if (this.executor30 == null || this.executor30.isShutdown()) {
            this.executor30 = Executors.newSingleThreadScheduledExecutor();
        }

        this.executor30.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Map map = AlivcMediaPublisher.getPerformanceMap();
                PublisherHeartBeatArgs args = new PublisherHeartBeatArgs();
                args.ts = AlivcMediaPublisher.getLong(map, "mTotalSizeOfUploadedPackets");
                args.tt = AlivcMediaPublisher.getLong(map, "mTotalTimeOfPublishing");
                PublisherHeartBeatEvent.sendEvent(args, AlivcMediaPublisher.this.mContext);
            }
        }, 0L, 30000L, TimeUnit.MILLISECONDS);
    }

    private void stop5Interval() {
        if (this.executor5 != null && !this.executor5.isShutdown()) {
            this.executor5.shutdown();
            this.executor5 = null;
        }

    }

    private void stop30Interval() {
        if (this.executor30 != null && !this.executor30.isShutdown()) {
            this.executor30.shutdown();
            this.executor30 = null;
        }

    }

    public static class SntpClient {
        private static final String TAG = "SntpClient";
        private static final int REFERENCE_TIME_OFFSET = 16;
        private static final int ORIGINATE_TIME_OFFSET = 24;
        private static final int RECEIVE_TIME_OFFSET = 32;
        private static final int TRANSMIT_TIME_OFFSET = 40;
        private static final int NTP_PACKET_SIZE = 48;
        private static final int NTP_PORT = 123;
        private static final int NTP_MODE_CLIENT = 3;
        private static final int NTP_VERSION = 3;
        private static final long OFFSET_1900_TO_1970 = 2208988800L;
        private long mNtpTime;
        private long mNtpTimeReference;
        private long mRoundTripTime;

        public SntpClient() {
        }

        public boolean requestTime(String host, int timeout) {
            DatagramSocket socket = null;

            boolean buffer;
            try {
                socket = new DatagramSocket();
                socket.setSoTimeout(timeout);
                InetAddress e = InetAddress.getByName(host);
                byte[] buffer1 = new byte[48];
                DatagramPacket request = new DatagramPacket(buffer1, buffer1.length, e, 123);
                buffer1[0] = 27;
                long requestTime = System.currentTimeMillis();
                long requestTicks = SystemClock.elapsedRealtime();
                this.writeTimeStamp(buffer1, 40, requestTime);
                socket.send(request);
                DatagramPacket response = new DatagramPacket(buffer1, buffer1.length);
                socket.receive(response);
                long responseTicks = SystemClock.elapsedRealtime();
                long responseTime = requestTime + (responseTicks - requestTicks);
                long originateTime = this.readTimeStamp(buffer1, 24);
                long receiveTime = this.readTimeStamp(buffer1, 32);
                long transmitTime = this.readTimeStamp(buffer1, 40);
                long roundTripTime = responseTicks - requestTicks - (transmitTime - receiveTime);
                long clockOffset = (receiveTime - originateTime + (transmitTime - responseTime)) / 2L;
                this.mNtpTime = responseTime + clockOffset;
                this.mNtpTimeReference = responseTicks;
                this.mRoundTripTime = roundTripTime;
                return true;
            } catch (Exception var29) {
                buffer = false;
            } finally {
                if (socket != null) {
                    socket.close();
                }

            }

            return buffer;
        }

        public long getNtpTime() {
            return this.mNtpTime;
        }

        public long getNtpTimeReference() {
            return this.mNtpTimeReference;
        }

        public long getRoundTripTime() {
            return this.mRoundTripTime;
        }

        private long read32(byte[] buffer, int offset) {
            byte b0 = buffer[offset];
            byte b1 = buffer[offset + 1];
            byte b2 = buffer[offset + 2];
            byte b3 = buffer[offset + 3];
            int i0 = (b0 & 128) == 128 ? (b0 & 127) + 128 : b0;
            int i1 = (b1 & 128) == 128 ? (b1 & 127) + 128 : b1;
            int i2 = (b2 & 128) == 128 ? (b2 & 127) + 128 : b2;
            int i3 = (b3 & 128) == 128 ? (b3 & 127) + 128 : b3;
            return ((long) i0 << 24) + ((long) i1 << 16) + ((long) i2 << 8) + (long) i3;
        }

        private long readTimeStamp(byte[] buffer, int offset) {
            long seconds = this.read32(buffer, offset);
            long fraction = this.read32(buffer, offset + 4);
            return (seconds - 2208988800L) * 1000L + fraction * 1000L / 4294967296L;
        }

        private void writeTimeStamp(byte[] buffer, int offset, long time) {
            long seconds = time / 1000L;
            long milliseconds = time - seconds * 1000L;
            seconds += 2208988800L;
            buffer[offset++] = (byte) ((int) (seconds >> 24));
            buffer[offset++] = (byte) ((int) (seconds >> 16));
            buffer[offset++] = (byte) ((int) (seconds >> 8));
            buffer[offset++] = (byte) ((int) (seconds >> 0));
            long fraction = milliseconds * 4294967296L / 1000L;
            buffer[offset++] = (byte) ((int) (fraction >> 24));
            buffer[offset++] = (byte) ((int) (fraction >> 16));
            buffer[offset++] = (byte) ((int) (fraction >> 8));
            buffer[offset++] = (byte) ((int) (Math.random() * 255.0D));
        }
    }
}
