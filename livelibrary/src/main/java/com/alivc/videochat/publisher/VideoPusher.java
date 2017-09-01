//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;

import com.alivc.videochat.PhoneAdapterUtil;
import com.alivc.videochat.publisher.IMediaPublisher.OnPreparedListener;
import com.alivc.videochat.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VideoPusher implements PreviewCallback {
    public static final int RESOLUTION_480 = 480;
    public static final int RESOLUTION_1280 = 1280;
    public static final int RESOLUTION_720 = 720;
    public static final int RESOLUTION_640 = 640;
    public static final int RESOLUTION_360 = 360;
    public static final int RESOLUTION_848 = 848;
    private static final String TAG = "VideoPusher";
    private static final int SCREEN_PORTRAIT = 0;
    private static final int SCREEN_LANDSCAPE_LEFT = 90;
    private static final int SCREEN_LANDSCAPE_RIGHT = 270;
    private static byte[] buffer = null;
    private static OnPreparedListener sOnPreparedListener = null;
    private boolean mPreviewRunning;
    private Camera mCamera;
    private VideoParam mParam;
    private int mScreen;
    private int mOrientation;
    private boolean mPause = false;
    private boolean mUseEmptyData = false;
    private VideoPusher.VideoSourceListener mVideoSourceListener;
    private byte[] mBlackScreen;
    private boolean mSwitchCamera = false;
    private boolean mNotifyPrepared = true;
    private long mLastCaptureTime = 0L;
    private long mLastFpsCountTime = 0L;
    private int mLastFpsCounter = 0;
    private int mBelowMinFpsNumberTimes = 0;
    private int mCurrentFps = 0;
    private Handler mHandler;
    private SurfaceTexture mSurfaceTexture = new SurfaceTexture(10);

    public VideoPusher(Context context, Handler handler, VideoParam param) {
        this.mParam = param;
        this.mHandler = handler;
    }

    private static void turnLightOn(Camera mCamera) {
        if(mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            if(parameters != null) {
                List flashModes = parameters.getSupportedFlashModes();
                if(flashModes != null) {
                    String flashMode = parameters.getFlashMode();
                    if(!"torch".equals(flashMode) && flashModes.contains("torch")) {
                        parameters.setFlashMode("torch");
                        mCamera.setParameters(parameters);
                    }

                }
            }
        }
    }

    private static void turnLightOff(Camera mCamera) {
        if(mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            if(parameters != null) {
                List flashModes = parameters.getSupportedFlashModes();
                String flashMode = parameters.getFlashMode();
                if(flashModes != null) {
                    if(!"off".equals(flashMode)) {
                        if(flashModes.contains("off")) {
                            parameters.setFlashMode("off");
                            mCamera.setParameters(parameters);
                        } else {
                            Log.e("VideoPusher", "FLASH_MODE_OFF not supported");
                        }
                    }

                }
            }
        }
    }

    public static void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        sOnPreparedListener = onPreparedListener;
    }

    public void release() {
        LogUtil.d("VideoPusher", "release.");
        this.stopPreview();
        this.mSurfaceTexture.release();
    }

    public void switchCamera() throws Exception {
        LogUtil.d("VideoPusher", "switchCamera.");
        this.mSwitchCamera = true;
        if(this.mParam.getCameraId() == 0) {
            this.mParam.setCameraId(1);
        } else {
            this.mParam.setCameraId(0);
        }

        this.stopPreview();
        this.startPreview();
    }

    public void reset() {
        if(this.mPreviewRunning) {
            ;
        }
    }

    public void stopPreview() {
        LogUtil.d("VideoPusher", "stopPreview.");
        if(this.mPreviewRunning && this.mCamera != null) {
            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.mPreviewRunning = false;
            LogUtil.d("VideoPusher", "stopPreview over.");
        }

    }

    public void startPreview() throws Exception {
        LogUtil.d("VideoPusher", "startPreview.");

        try {
            this.startPreview0();
        } catch (Throwable var4) {
            this.stopPreview();

            try {
                this.startPreview0();
            } catch (Exception var3) {
                throw var3;
            }
        }

    }

    private void startPreview0() throws Exception {
        if(!this.mPreviewRunning) {
            this.mCamera = Camera.open(this.mParam.getCameraId());
            Parameters parameters = this.mCamera.getParameters();
            parameters.setPreviewFormat(17);
            if(parameters.isZoomSupported()) {
                parameters.setZoom(this.mParam.getCurrentZoom());
                this.mParam.setMaxZoom(parameters.getMaxZoom());
            }

            this.setPreviewSize(parameters);
            this.setPreviewFpsRange(parameters);
            this.setPreviewOrientation(parameters, this.mParam.getRotation());
            this.mCamera.setParameters(parameters);
            if(buffer == null) {
                buffer = new byte[this.mParam.getWidth() * this.mParam.getHeight() * 3 / 2];
            }

            this.mCamera.addCallbackBuffer(buffer);
            this.mCamera.setPreviewCallbackWithBuffer(this);
            this.mCamera.setPreviewTexture(this.mSurfaceTexture);
            this.mCamera.startPreview();
            this.mPreviewRunning = true;
            Log.e("VideoPusher", "start preivew over.");
        }
    }

    public void pausePreview(boolean useEmptyData) {
        LogUtil.d("VideoPusher", "pause preview.");
        this.mPause = true;
    }

    public void resumePreview() {
        LogUtil.d("VideoPusher", "resume preview.");
        this.mPause = false;
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if(System.currentTimeMillis() - VideoPusher.this.mLastCaptureTime > 1000L) {
                    VideoPusher.this.mHandler.sendEmptyMessage(410);
                }

            }
        }, 1000L);
    }

    private void setPreviewSize(Parameters parameters) {
        List supportedPreviewFormats = parameters.getSupportedPreviewFormats();
        Iterator supportedPreviewSizes = supportedPreviewFormats.iterator();

        while(supportedPreviewSizes.hasNext()) {
            Integer size = (Integer)supportedPreviewSizes.next();
            System.out.println("支持:" + size);
        }

        List supportedPreviewSizes1 = parameters.getSupportedPreviewSizes();
        Size size1 = null;
        Iterator iterator = supportedPreviewSizes1.iterator();

        while(iterator.hasNext()) {
            Size next = (Size)iterator.next();
            LogUtil.d("VideoPusher", "支持 " + next.width + "x" + next.height);
            if(next.width == this.mParam.getWidth() && next.height == this.mParam.getHeight() || next.width == this.mParam.getHeight() && next.height == this.mParam.getWidth()) {
                size1 = next;
                break;
            }
        }

        if(size1 == null) {
            this.mParam.setHeight(480);
            this.mParam.setWidth(640);
            this.mParam.setPushWidth(640);
            this.mParam.setPushHeight(480);
        }

        parameters.setPreviewSize(this.mParam.getWidth(), this.mParam.getHeight());
        this.mBlackScreen = new byte[this.mParam.getWidth() * this.mParam.getHeight() * 3 / 2];
        LogUtil.d("VideoPusher", "预览分辨率 width:" + this.mParam.getWidth() + " height:" + this.mParam.getHeight());
    }

    private void setPreviewFpsRange(Parameters parameters) {
        int[] range = new int[2];
        parameters.getPreviewFpsRange(range);
        parameters.setPreviewFrameRate(30);
        LogUtil.d("VideoPusher", "预览帧率 fps:" + range[0] + " - " + range[1]);
    }

    private void preparePublisher(int pushWidth, int pushHeight) {
        LogUtil.d("VideoPusher", "prepare publisher. " + this.mOrientation + " " + this.mParam.getCameraId());
        if(!this.mSwitchCamera && !this.mPause) {
            NativeVideoCallPublisher.getInstance().preparePublisher(this.mParam.getWidth(), this.mParam.getHeight(), pushWidth, pushHeight, this.mParam.getCameraId(), this.mOrientation, PhoneAdapterUtil.getSkipMicOffset());
            if(sOnPreparedListener != null && this.mNotifyPrepared) {
                sOnPreparedListener.onPrepared();
            }

            LogUtil.d("VideoPusher", "prepare publisher over.");
        } else {
            this.mSwitchCamera = false;
        }

    }

    private void setPreviewOrientation(Parameters parameters, int rotation) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(this.mParam.getCameraId(), info);
        System.out.println("xb1205: " + info.orientation);
        this.mScreen = 0;
        switch(rotation) {
            case 0:
                this.mScreen = 0;
                if(info.facing == 1) {
                    this.mOrientation = (info.orientation + this.mScreen) % 360;
                    this.mOrientation = (360 - this.mOrientation) % 360;
                } else {
                    this.mOrientation = (info.orientation - this.mScreen + 360) % 360;
                }

                this.preparePublisher(this.mParam.getPushHeight(), this.mParam.getPushWidth());
                break;
            case 1:
                this.mScreen = 90;
                if(info.facing == 1) {
                    this.mOrientation = (info.orientation + this.mScreen) % 360;
                    this.mOrientation = (360 - this.mOrientation) % 360;
                } else {
                    this.mOrientation = (info.orientation - this.mScreen + 360) % 360;
                }

                this.preparePublisher(this.mParam.getPushWidth(), this.mParam.getPushHeight());
                break;
            case 2:
                this.mScreen = 180;
                break;
            case 3:
                this.mScreen = 270;
                if(info.facing == 1) {
                    this.mOrientation = (info.orientation + this.mScreen) % 360;
                    this.mOrientation = (360 - this.mOrientation) % 360;
                } else {
                    this.mOrientation = (info.orientation - this.mScreen + 360) % 360;
                }

                this.preparePublisher(this.mParam.getPushWidth(), this.mParam.getPushHeight());
        }

        this.mNotifyPrepared = true;
        this.mCamera.setDisplayOrientation(this.mOrientation);
    }

    public void setSurfaceChanged() {
        LogUtil.d("VideoPusher", "surface changed.");
        this.stopPreview();

        try {
            this.startPreview();
        } catch (Throwable var2) {
            var2.printStackTrace();
        }

    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d("VideoPusher", "onPreviewFrame");
        if(this.mVideoSourceListener != null) {
            if(this.mPause) {
                this.mVideoSourceListener.onVideoFrame(this.mBlackScreen, this.mOrientation);
            } else {
                this.mVideoSourceListener.onVideoFrame(data, this.mOrientation);
                System.arraycopy(data, 0, this.mBlackScreen, 0, this.mBlackScreen.length);
            }
        }

        this.mLastCaptureTime = System.currentTimeMillis();
        ++this.mLastFpsCounter;
        if(this.mLastFpsCountTime == 0L) {
            this.mLastFpsCountTime = this.mLastCaptureTime;
        }

        if(this.mLastCaptureTime - this.mLastFpsCountTime > 1000L) {
            Log.i("VideoPusher", "StatLog: video capture fps = " + this.mLastFpsCounter);
            this.mCurrentFps = this.mLastFpsCounter;
            if(this.mLastFpsCounter < 12) {
                if(this.mBelowMinFpsNumberTimes > 5) {
                    this.mHandler.sendEmptyMessage(410);
                    this.mBelowMinFpsNumberTimes = 0;
                } else {
                    ++this.mBelowMinFpsNumberTimes;
                }
            } else {
                this.mBelowMinFpsNumberTimes = 0;
            }

            this.mLastFpsCounter = 0;
            this.mLastFpsCountTime = this.mLastCaptureTime;
        }

        camera.addCallbackBuffer(buffer);
    }

    public void setFlashOn(boolean flash) {
        if(this.mCamera != null) {
            if(flash) {
                turnLightOn(this.mCamera);
            } else {
                turnLightOff(this.mCamera);
            }
        }

    }

    public void setAutoFocus(boolean autoFocus) {
        if(this.mCamera != null) {
            this.mCamera.cancelAutoFocus();
            Parameters parameters = this.mCamera.getParameters();
            if(autoFocus) {
                parameters.setFocusMode("auto");
            } else {
                parameters.setFocusMode("continuous-video");
            }

            this.mCamera.setParameters(parameters);
            this.mCamera.autoFocus((AutoFocusCallback)null);
        }

    }

    public void setZoom(float scaleFactor) {
        if(this.mCamera != null) {
            Parameters parameters = this.mCamera.getParameters();
            if(parameters.isZoomSupported()) {
                int zoomScaled = (int)(scaleFactor * (float)this.mParam.getCurrentZoom());
                if(zoomScaled <= 1) {
                    this.mParam.setCurrentZoom(1);
                } else if(zoomScaled >= parameters.getMaxZoom()) {
                    this.mParam.setCurrentZoom(parameters.getMaxZoom());
                } else {
                    this.mParam.setCurrentZoom(zoomScaled);
                }

                parameters.setZoom(this.mParam.getCurrentZoom());
                this.mCamera.setParameters(parameters);
            }
        }

    }

    private int getSuitableZoomIndex(List<Integer> zoomList, int target_zoom) {
        int zoom_max = ((Integer)zoomList.get(zoomList.size() - 1)).intValue();
        target_zoom = Math.min(target_zoom, zoom_max);
        int best_diff = zoom_max;
        int best_idx = 0;
        int i = 0;

        for(int count = zoomList.size(); i < count; ++i) {
            int current_value = ((Integer)zoomList.get(i)).intValue();
            int current_diff = Math.abs(current_value - target_zoom);
            if(current_diff < best_diff) {
                best_idx = i;
                best_diff = current_diff;
            }
        }

        return best_idx;
    }

    public void setFocus(float xRatio, float yRatio) {
        if(this.mCamera != null) {
            Parameters parameters = this.mCamera.getParameters();
            int x = (int)(xRatio * 2000.0F - 1000.0F);
            int y = (int)(yRatio * 2000.0F - 1000.0F);
            Area area = new Area(new Rect(x - 50, y - 50, x + 50, y + 50), 1);
            ArrayList areas = new ArrayList();
            areas.add(area);
            parameters.setFocusAreas(areas);

            try {
                this.mCamera.setParameters(parameters);
            } catch (Throwable var9) {
                var9.printStackTrace();
            }

            this.mCamera.autoFocus(new AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.cancelAutoFocus();
                }
            });
        }
    }

    public void setVideoSourceListener(VideoPusher.VideoSourceListener listener) {
        this.mVideoSourceListener = listener;
    }

    public long getLastCaptureTime() {
        return this.mLastCaptureTime;
    }

    public void setLastCaptureTime(long lastCaptureTime) {
        this.mLastCaptureTime = lastCaptureTime;
    }

    public int getCurrentFps() {
        return this.mCurrentFps;
    }

    public interface VideoSourceListener {
        void onVideoFrame(byte[] var1, int var2);
    }
}
