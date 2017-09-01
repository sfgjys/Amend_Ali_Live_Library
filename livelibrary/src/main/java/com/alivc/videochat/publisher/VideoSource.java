//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceView;

import com.alivc.videochat.publisher.VideoPusher.VideoSourceListener;
import com.alivc.videochat.utils.LogUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class VideoSource {
    private static final String TAG = "VideoSource";
    private static final int MAGIC_TEXTURE_ID = 10;
    private Activity mContext;
    private Camera mCamera;
    private VideoParam mVideoParam;
    private SurfaceView mSurfaceView;
    private byte[] mFrameBuffer;
    private byte[] raw;
    private boolean bIfPreview = false;
    private VideoSourceListener mVideoSourceListener;
    private int mCaptureFps = 0;
    private int mCaptureCount = 0;
    private long mLastCaptureTimestamps = 0L;
    private int screen;
    private static final int SCREEN_PORTRAIT = 0;
    private static final int SCREEN_LANDSCAPE_LEFT = 90;
    private static final int SCREEN_LANDSCAPE_RIGHT = 270;
    private PreviewCallback mPreviewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            switch(VideoSource.this.screen) {
                case 0:
                    VideoSource.this.portraitData2Raw(data);
                    break;
                case 90:
                    VideoSource.this.raw = data;
                    break;
                case 270:
                    VideoSource.this.raw = data;
            }

            if(data != null && data.length > 0 && VideoSource.this.mVideoSourceListener != null) {
                VideoSource.this.mVideoSourceListener.onVideoFrame(VideoSource.this.raw, 0);
            }

            VideoSource.this.mCamera.addCallbackBuffer(VideoSource.this.mFrameBuffer);
            VideoSource.this.mCaptureCount++;
            long currentTimestamps = System.currentTimeMillis();
            if(currentTimestamps - VideoSource.this.mLastCaptureTimestamps > 1000L) {
                VideoSource.this.mCaptureFps = VideoSource.this.mCaptureCount;
                VideoSource.this.mCaptureCount = 0;
                VideoSource.this.mLastCaptureTimestamps = currentTimestamps;
            }

        }
    };

    public VideoSource(Activity context, VideoParam videoParam, SurfaceView surfaceView) {
        this.mContext = context;
        this.mVideoParam = videoParam;
        this.mSurfaceView = surfaceView;
        this.mFrameBuffer = new byte[this.mVideoParam.getWidth() * this.mVideoParam.getHeight() * 2];
        this.raw = new byte[this.mVideoParam.getWidth() * this.mVideoParam.getHeight() * 3 / 2];
    }

    public void setVideoSourceListener(VideoSourceListener listener) {
        this.mVideoSourceListener = listener;
    }

    public void startPreview() {
        this.mCamera = Camera.open(this.mVideoParam.getCameraId());

        try {
            SurfaceTexture exception = new SurfaceTexture(10);
            this.mCamera.setPreviewTexture(exception);
        } catch (IOException var2) {
            if(null != this.mCamera) {
                this.mCamera.release();
                this.mCamera = null;
            }
        }

        this.mCamera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
        this.mCamera.addCallbackBuffer(this.mFrameBuffer);
        this.initCamera();
        this.mCamera.startPreview();
        this.bIfPreview = true;
    }

    public void stopPreview() {
        if(null != this.mCamera) {
            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }

    }

    private void initCamera() {
        if(this.bIfPreview) {
            this.mCamera.stopPreview();
        }

        if(null != this.mCamera) {
            Parameters parameters = this.mCamera.getParameters();
            parameters.setPreviewFormat(17);
            this.setPreviewSize(parameters);
            this.setPreviewFpsRange(parameters);
            this.setPreviewOrientation(parameters);
            this.mCamera.setParameters(parameters);
        }

    }

    private void setPreviewSize(Parameters parameters) {
        List supportedPreviewFormats = parameters.getSupportedPreviewFormats();
        Iterator supportedPreviewSizes = supportedPreviewFormats.iterator();

        while(supportedPreviewSizes.hasNext()) {
            Integer size = (Integer)supportedPreviewSizes.next();
            System.out.println("支持:" + size);
        }

        List supportedPreviewSizes1 = parameters.getSupportedPreviewSizes();
        Size size1 = (Size)supportedPreviewSizes1.get(0);
        LogUtil.d("VideoSource", "支持 " + size1.width + "x" + size1.height);
        int m = Math.abs(size1.height * size1.width - this.mVideoParam.getHeight() * this.mVideoParam.getWidth());
        supportedPreviewSizes1.remove(0);
        Iterator iterator = supportedPreviewSizes1.iterator();

        while(iterator.hasNext()) {
            Size next = (Size)iterator.next();
            LogUtil.d("VideoSource", "支持 " + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - this.mVideoParam.getHeight() * this.mVideoParam.getWidth());
            if(n < m) {
                m = n;
                size1 = next;
            }
        }

        this.mVideoParam.setHeight(size1.height);
        this.mVideoParam.setWidth(size1.width);
        parameters.setPreviewSize(this.mVideoParam.getWidth(), this.mVideoParam.getHeight());
        LogUtil.d("VideoSource", "预览分辨率 width:" + size1.width + " height:" + size1.height);
    }

    private void setPreviewFpsRange(Parameters parameters) {
        int[] range = new int[2];
        parameters.getPreviewFpsRange(range);
        LogUtil.d("VideoSource", "预览帧率 fps:" + range[0] + " - " + range[1]);
    }

    private void setPreviewOrientation(Parameters parameters) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(this.mVideoParam.getCameraId(), info);
        int rotation = this.mContext.getWindowManager().getDefaultDisplay().getRotation();
        this.screen = 0;
        switch(rotation) {
            case 0:
                this.screen = 0;
                break;
            case 1:
                this.screen = 90;
                break;
            case 2:
                this.screen = 180;
                break;
            case 3:
                this.screen = 270;
        }

        int result;
        if(info.facing == 1) {
            result = (info.orientation + this.screen) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - this.screen + 360) % 360;
        }

        this.mCamera.setDisplayOrientation(result);
    }

    public int getVideoCaptureFps() {
        return this.mCaptureFps;
    }

    private void landscapeData2Raw(byte[] data) {
        int width = this.mVideoParam.getWidth();
        int height = this.mVideoParam.getHeight();
        int y_len = width * height;
        int k = 0;

        int maxpos;
        for(maxpos = y_len - 1; maxpos > -1; --maxpos) {
            this.raw[k] = data[maxpos];
            ++k;
        }

        maxpos = data.length - 1;
        int uv_len = y_len >> 2;

        for(int i = 0; i < uv_len; ++i) {
            int pos = i << 1;
            this.raw[y_len + i * 2] = data[maxpos - pos - 1];
            this.raw[y_len + i * 2 + 1] = data[maxpos - pos];
        }

    }

    private void portraitData2Raw(byte[] data) {
        int width = this.mVideoParam.getWidth();
        int height = this.mVideoParam.getHeight();
        int y_len = width * height;
        int uvHeight = height >> 1;
        int k = 0;
        int i;
        int nPos;
        if(this.mVideoParam.getCameraId() == 0) {
            for(i = 0; i < width; ++i) {
                for(nPos = height - 1; nPos >= 0; --nPos) {
                    this.raw[k++] = data[width * nPos + i];
                }
            }

            for(i = 0; i < width; i += 2) {
                for(nPos = uvHeight - 1; nPos >= 0; --nPos) {
                    this.raw[k++] = data[y_len + width * nPos + i];
                    this.raw[k++] = data[y_len + width * nPos + i + 1];
                }
            }
        } else {
            int j;
            for(i = 0; i < width; ++i) {
                nPos = width - 1;

                for(j = 0; j < height; ++j) {
                    this.raw[k] = data[nPos - i];
                    ++k;
                    nPos += width;
                }
            }

            for(i = 0; i < width; i += 2) {
                nPos = y_len + width - 1;

                for(j = 0; j < uvHeight; ++j) {
                    this.raw[k] = data[nPos - i - 1];
                    this.raw[k + 1] = data[nPos - i];
                    k += 2;
                    nPos += width;
                }
            }
        }

    }

    public void setSurfaceChanged() {
        this.stopPreview();
        this.startPreview();
    }

    public void switchCamera() {
        if(this.mVideoParam.getCameraId() == 0) {
            this.mVideoParam.setCameraId(1);
        } else {
            this.mVideoParam.setCameraId(0);
        }

        this.stopPreview();
        this.startPreview();
    }
}
