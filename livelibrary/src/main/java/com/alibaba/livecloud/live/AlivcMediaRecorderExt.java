//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

import android.content.Context;
import android.view.Surface;

import com.alivc.videochat.publisher.AlivcMediaPublisher;
import com.alivc.videochat.publisher.AlivcPublisherPerformanceInfo;
import com.alivc.videochat.publisher.IMediaPublisher;
import com.alivc.videochat.publisher.IMediaPublisher.OnErrorListener;

import java.util.HashMap;
import java.util.Map;

public class AlivcMediaRecorderExt implements AlivcMediaRecorder {
    private AlivcMediaPublisher mPublisher = new AlivcMediaPublisher();
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private OnLiveRecordErrorListener mRecordErrorListener;
    private OnRecordStatusListener mRecordStatusListener;
    private OnNetworkStatusListener mNetworkStatusListener;
    private OnErrorListener mPublishErrorListener = new OnErrorListener() {
        public boolean onError(IMediaPublisher mp, int errCode, String extra) {
            switch(errCode) {
                case -409:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -408:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -407:
                    if(AlivcMediaRecorderExt.this.mNetworkStatusListener != null) {
                        AlivcMediaRecorderExt.this.mNetworkStatusListener.onNetworkBusy();
                    }
                    break;
                case -406:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-110);
                    }
                    break;
                case -405:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -404:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -403:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-12);
                    }
                    break;
                case -402:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -401:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-1);
                    }
                    break;
                case -400:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-101);
                    }
                    break;
                case -200:
                    if(AlivcMediaRecorderExt.this.mRecordErrorListener != null) {
                        AlivcMediaRecorderExt.this.mRecordErrorListener.onError(-22);
                    }
                case 0:
            }

            return false;
        }
    };

    public AlivcMediaRecorderExt() {
    }

    public void init(Context context) {
        this.mContext = context;
        this.mPublisher.init(this.mContext);
    }

    public void prepare(Map<String, Object> params, Surface surface) {
        HashMap mediaParam = new HashMap();
        mediaParam.put("Rotation", "" + params.get("display-rotation"));
        this.mPublisher.prepare(surface, 360, 640, mediaParam);
        this.mPublisher.setOnErrorListener(this.mPublishErrorListener);
    }

    public void startRecord(String outputUrl) {
        this.mPublisher.start(outputUrl);
    }

    public int switchCamera() {
        this.mPublisher.switchCamera();
        return 0;
    }

    public void stopRecord() {
        if(this.mPublisher != null) {
            this.mPublisher.stop();
        }

    }

    public void reset() {
        this.mPublisher.stop();
    }

    public void focusing(float xRatio, float yRatio) {
        if(this.mPublisher != null) {
            this.mPublisher.setAutoFocusOn(true);
        }

    }

    public void autoFocus(float xRatio, float yRatio) {
        if(this.mPublisher != null) {
            this.mPublisher.setFocus(xRatio, yRatio);
        }

    }

    public boolean isFlagSupported(int flag) {
        return true;
    }

    public void setZoom(float scaleFactor) {
        if(this.mPublisher != null && scaleFactor > 1.0F && scaleFactor < 2.0F) {
            scaleFactor = 2.0F;
        }

        this.mPublisher.setZoom(scaleFactor);
    }

    public void setPreviewSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void addFlag(int flag) {
        if(this.mPublisher != null) {
            switch(flag) {
                case 1:
                    this.mPublisher.setBeautyOn(true);
                    break;
                case 4:
                    this.mPublisher.setAutoFocusOn(true);
                    break;
                case 8:
                    this.mPublisher.setSilentOn(true);
            }

        }
    }

    public void removeFlag(int flag) {
        if(this.mPublisher != null) {
            switch(flag) {
                case 1:
                    this.mPublisher.setBeautyOn(false);
                    break;
                case 4:
                    this.mPublisher.setAutoFocusOn(false);
                    break;
                case 8:
                    this.mPublisher.setSilentOn(false);
            }

        }
    }

    public void release() {
        this.mPublisher.release();
        this.mPublisher = null;
    }

    public void setOnRecordErrorListener(OnLiveRecordErrorListener listener) {
        this.mRecordErrorListener = listener;
    }

    public void setOnRecordStatusListener(OnRecordStatusListener listener) {
        this.mRecordStatusListener = listener;
    }

    public void setOnNetworkStatusListener(OnNetworkStatusListener listener) {
        this.mNetworkStatusListener = listener;
    }

    public AlivcRecordReporter getRecordReporter() {
        final AlivcPublisherPerformanceInfo performanceInfo = this.mPublisher.getPerformanceInfo();
        return new AlivcRecordReporter() {
            public int getInt(int key) {
                byte value = 0;
                return value;
            }

            public double getDouble(int key) {
                return 0.0D;
            }

            public long getLong(int key) {
                long value = 0L;
                switch(key) {
                    case 4109:
                        value = (long)performanceInfo.getVideoDurationFromeCaptureToUpload();
                    case 4:
                    default:
                        return value;
                }
            }

            public float getFloat(int key) {
                return 0.0F;
            }

            public boolean getBoolean(int key) {
                return false;
            }

            public String getString(int key) {
                return "";
            }

            public Object getValue(int key) {
                return "";
            }
        };
    }

    public AlivcMediaPublisher getPublisher() {
        return this.mPublisher;
    }

    public void pause() {
        if(this.mPublisher != null) {
            this.mPublisher.pause();
        }

    }

    public void resume(Surface surface) {
        if(this.mPublisher != null) {
            this.mPublisher.resume(surface);
        }

    }

    public void setFilterParam(Map<String, String> param) {
        if(this.mPublisher != null) {
            String value = (String)param.get("alivc_filter_param_beauty_on");
            if(value != null) {
                if(value.equals("true")) {
                    this.mPublisher.setBeautyOn(true);
                } else {
                    this.mPublisher.setBeautyOn(false);
                }
            }
        }

    }

    public void resetVideoPusher() {
        if(this.mPublisher != null) {
            this.mPublisher.resetVideoPusher();
        }

    }
}
