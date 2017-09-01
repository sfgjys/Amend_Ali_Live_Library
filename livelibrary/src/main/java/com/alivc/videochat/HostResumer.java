//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.videochat.player.AliVcMediaPlayer;
import com.alivc.videochat.publisher.AlivcMediaPublisher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HostResumer {
    public static final String TAG = "HostResumer";
    private SurfaceHolder mPublishSurfaceHolder;
    private SurfaceCallback mPublishSurfaceCallback;
    private Map<SurfaceHolder, SurfaceCallback> mPlayerSurfaceViewMap = new HashMap();

    public HostResumer() {
    }

    private static boolean isAllRecreated(Collection<SurfaceCallback> callbackList) {
        Iterator i$ = callbackList.iterator();

        SurfaceCallback callback;
        do {
            if(!i$.hasNext()) {
                return true;
            }

            callback = (SurfaceCallback)i$.next();
        } while(callback.getSurfaceStatus() == SurfaceStatus.RECREATED);

        return false;
    }

    public void setPublishSurfaceHolder(SurfaceHolder publishSurfaceHolder) {
        if(publishSurfaceHolder != null) {
            this.mPublishSurfaceHolder = publishSurfaceHolder;
            this.mPublishSurfaceCallback = new SurfaceCallback(this.mPublishSurfaceHolder);
        } else {
            this.mPublishSurfaceHolder = null;
            this.mPublishSurfaceCallback = null;
        }

    }

    public void addPlayerSurfaceHolder(SurfaceHolder playerSurfaceView) {
        if(playerSurfaceView != null) {
            SurfaceCallback surfaceCallback = new SurfaceCallback(playerSurfaceView);
            this.mPlayerSurfaceViewMap.put(playerSurfaceView, surfaceCallback);
        }

    }

    public void removePlayerSurfaceHolder(SurfaceHolder playerSurfaceView) {
        if(playerSurfaceView != null) {
            this.mPlayerSurfaceViewMap.remove(playerSurfaceView);
        }

    }

    public void clearPlayerSurface() {
        this.mPlayerSurfaceViewMap.clear();
    }

    public void resume(final AlivcMediaPublisher publisher, final Map<String, AliVcMediaPlayer> mediaPlayerMap, final Map<String, SurfaceView> surfaceViewMap) {
        if(this.mPublishSurfaceCallback != null && this.mPublishSurfaceHolder != null) {
            if(this.mPublishSurfaceCallback.getSurfaceStatus() != SurfaceStatus.DESTROYED) {
                Iterator callback = mediaPlayerMap.keySet().iterator();

                while(callback.hasNext()) {
                    String i$ = (String)callback.next();
                    AliVcMediaPlayer surfaceCallback = (AliVcMediaPlayer)mediaPlayerMap.get(i$);
                    SurfaceView surfaceView = (SurfaceView)surfaceViewMap.get(i$);
                    if(surfaceCallback != null) {
                        if(surfaceView != null) {
                            surfaceCallback.setVideoSurface(surfaceView.getHolder().getSurface());
                        }

                        surfaceCallback.play();
                    }
                }

                publisher.resume((Surface)null);
            } else {
                Log.d("HostResumer", "surface destroyed.");
                if(this.mPublishSurfaceCallback.getResumeCallback() == null) {
                    Log.d("HostResumer", "new callback.");
                    Callback callback1 = new Callback() {
                        public void onEvent() {
                            Log.d("HostResumer", "on event.");
                            if(HostResumer.this.mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED && HostResumer.isAllRecreated(HostResumer.this.mPlayerSurfaceViewMap.values())) {
                                Iterator i$ = surfaceViewMap.keySet().iterator();

                                while(i$.hasNext()) {
                                    String url = (String)i$.next();
                                    AliVcMediaPlayer player = (AliVcMediaPlayer)mediaPlayerMap.get(url);
                                    if(player != null) {
                                        player.setVideoSurface(((SurfaceView)surfaceViewMap.get(url)).getHolder().getSurface());
                                        player.play();
                                    }
                                }

                                if(publisher != null && HostResumer.this.mPublishSurfaceHolder != null) {
                                    publisher.resume(HostResumer.this.mPublishSurfaceHolder.getSurface());
                                }
                            }

                        }
                    };
                    Log.d("HostResumer", "set callback for publish.");
                    this.mPublishSurfaceCallback.setResumeCallback(callback1);
                    Iterator i$1 = this.mPlayerSurfaceViewMap.values().iterator();

                    while(i$1.hasNext()) {
                        SurfaceCallback surfaceCallback1 = (SurfaceCallback)i$1.next();
                        Log.d("HostResumer", "set callback for player.");
                        surfaceCallback1.setResumeCallback(callback1);
                    }
                }
            }

        }
    }

    public void release() {
        this.clearPlayerSurface();
        if(this.mPublishSurfaceCallback != null) {
            this.mPublishSurfaceCallback.release();
            this.mPublishSurfaceCallback = null;
        }

        this.mPublishSurfaceHolder = null;
    }
}
