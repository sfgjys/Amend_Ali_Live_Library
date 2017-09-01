//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.view.SurfaceHolder;

public class SurfaceCallback {
    private SurfaceHolder mSurfaceHolder;
    private SurfaceStatus mSurfaceStatus;
    private Callback mResumeCallback;
    private SurfaceHolder.Callback mCallback;

    public SurfaceCallback(SurfaceHolder surfaceHolder) {
        this.mSurfaceStatus = SurfaceStatus.CREATED;
        this.mSurfaceHolder = surfaceHolder;
        this.mCallback = new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                if(SurfaceCallback.this.mSurfaceStatus == SurfaceStatus.UNINITED) {
                    SurfaceCallback.this.mSurfaceStatus = SurfaceStatus.CREATED;
                } else if(SurfaceCallback.this.mSurfaceStatus == SurfaceStatus.DESTROYED) {
                    SurfaceCallback.this.mSurfaceStatus = SurfaceStatus.RECREATED;
                    if(SurfaceCallback.this.mResumeCallback != null) {
                        SurfaceCallback.this.mResumeCallback.onEvent();
                    }
                }

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                SurfaceCallback.this.mSurfaceStatus = SurfaceStatus.DESTROYED;
            }
        };
        this.mSurfaceHolder.addCallback(this.mCallback);
    }

    public SurfaceStatus getSurfaceStatus() {
        return this.mSurfaceStatus;
    }

    public Callback getResumeCallback() {
        return this.mResumeCallback;
    }

    public void setResumeCallback(Callback resumeCallback) {
        this.mResumeCallback = resumeCallback;
    }

    public void release() {
        this.mSurfaceHolder.removeCallback(this.mCallback);
        this.mSurfaceHolder = null;
        this.mSurfaceStatus = SurfaceStatus.CREATED;
        this.mResumeCallback = null;
        this.mCallback = null;
    }
}
