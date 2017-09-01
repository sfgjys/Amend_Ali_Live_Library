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

public class ParterResumer {
    private SurfaceHolder mPublishSurfaceHolder;
    private SurfaceHolder mHostPlayerSurfaceHolder;
    private SurfaceCallback mPublishSurfaceCallback;
    private SurfaceCallback mHostPlayerSurfaceCallback;
    private Map<SurfaceHolder, SurfaceCallback> mPlayerSurfaceViewMap = new HashMap();

    public ParterResumer() {
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
        this.mPublishSurfaceHolder = publishSurfaceHolder;
        this.mPublishSurfaceCallback = new SurfaceCallback(this.mPublishSurfaceHolder);
    }

    public void setHostPlayerSurfaceHolder(SurfaceHolder hostPlayerSurfaceHolder) {
        if(hostPlayerSurfaceHolder != null) {
            this.mHostPlayerSurfaceHolder = hostPlayerSurfaceHolder;
            this.mHostPlayerSurfaceCallback = new SurfaceCallback(this.mHostPlayerSurfaceHolder);
        } else {
            this.mHostPlayerSurfaceHolder = null;
            this.mHostPlayerSurfaceCallback = null;
        }

    }

    public void addPlayerSurfaceHolder(SurfaceHolder playerSurfaceView) {
        SurfaceCallback surfaceCallback = new SurfaceCallback(playerSurfaceView);
        this.mPlayerSurfaceViewMap.put(playerSurfaceView, surfaceCallback);
    }

    public void removePlayerSurfaceHolder(SurfaceHolder playerSurfaceView) {
        ((SurfaceCallback)this.mPlayerSurfaceViewMap.get(playerSurfaceView)).release();
        this.mPlayerSurfaceViewMap.remove(playerSurfaceView);
    }

    public void clearPlayerSurfaceView() {
        Iterator i$ = this.mPlayerSurfaceViewMap.values().iterator();

        while(i$.hasNext()) {
            SurfaceCallback callback = (SurfaceCallback)i$.next();
            callback.release();
        }

        this.mPlayerSurfaceViewMap.clear();
    }

    public void resume(final AlivcMediaPublisher publisher, final AliVcMediaPlayer hostPlayer, final Map<String, AliVcMediaPlayer> mediaPlayerMap, final Map<String, SurfaceView> surfaceViewMap) {
        Log.d("xb01", "resume");
        if(this.mHostPlayerSurfaceCallback != null) {
            if(this.mHostPlayerSurfaceCallback != null && this.mHostPlayerSurfaceCallback.getSurfaceStatus() != SurfaceStatus.DESTROYED) {
                if(hostPlayer != null) {
                    if(this.mHostPlayerSurfaceHolder != null) {
                        hostPlayer.setVideoSurface(this.mHostPlayerSurfaceHolder.getSurface());
                        Log.d("xb01:", "host player set video surface when surface not destroyed");
                    }

                    hostPlayer.play();
                    Log.d("xb01", "host player play when surface not destroyed.");
                }

                Iterator callback1 = mediaPlayerMap.keySet().iterator();

                while(callback1.hasNext()) {
                    String i$1 = (String)callback1.next();
                    AliVcMediaPlayer surfaceCallback1 = (AliVcMediaPlayer)mediaPlayerMap.get(i$1);
                    SurfaceView surfaceView = (SurfaceView)surfaceViewMap.get(i$1);
                    if(surfaceCallback1 != null) {
                        if(surfaceView != null) {
                            surfaceCallback1.setVideoSurface(surfaceView.getHolder().getSurface());
                            Log.d("xb01:", "player set video surface when surface not destroyed");
                        }

                        surfaceCallback1.play();
                        Log.d("xb01", "host player play when surface not destroyed.");
                    }
                }

                if(publisher != null) {
                    publisher.resume((Surface)null);
                    Log.d("xb01", "publish resume when surface not destroy.");
                }
            } else if(this.mHostPlayerSurfaceCallback != null && this.mHostPlayerSurfaceCallback.getResumeCallback() == null) {
                Callback callback = new Callback() {
                    public void onEvent() {
                        Log.d("xb01", "on event");
                        Log.d("xb01", "mPublishSurfaceCallback == null ? " + (ParterResumer.this.mPublishSurfaceCallback == null));
                        if(ParterResumer.this.mPublishSurfaceCallback != null) {
                            Log.d("xb01", "mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED ? " + (ParterResumer.this.mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED));
                            Log.d("xb01", "mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.CREATED ? " + (ParterResumer.this.mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.CREATED));
                        }

                        if(ParterResumer.this.mHostPlayerSurfaceCallback != null) {
                            Log.d("xb01", " mHostPlayerSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED ? " + (ParterResumer.this.mHostPlayerSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED));
                        }

                        if((ParterResumer.this.mPublishSurfaceCallback == null || ParterResumer.this.mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED || ParterResumer.this.mPublishSurfaceCallback.getSurfaceStatus() == SurfaceStatus.CREATED) && ParterResumer.this.mHostPlayerSurfaceCallback.getSurfaceStatus() == SurfaceStatus.RECREATED) {
                            if(ParterResumer.isAllRecreated(ParterResumer.this.mPlayerSurfaceViewMap.values())) {
                                Log.d("xb01", "all recreated when surface destroy.");
                                if(hostPlayer != null) {
                                    if(hostPlayer != null) {
                                        hostPlayer.setVideoSurface(ParterResumer.this.mHostPlayerSurfaceHolder.getSurface());
                                        Log.d("xb01", "host player set video surface when surface destroy.");
                                    }

                                    hostPlayer.play();
                                    Log.d("xb01", "host player play when surface destroy..");
                                }

                                Iterator i$ = surfaceViewMap.keySet().iterator();

                                while(i$.hasNext()) {
                                    String url = (String)i$.next();
                                    AliVcMediaPlayer player = (AliVcMediaPlayer)mediaPlayerMap.get(url);
                                    if(player != null) {
                                        if(surfaceViewMap.get(url) != null) {
                                            player.setVideoSurface(((SurfaceView)surfaceViewMap.get(url)).getHolder().getSurface());
                                            Log.d("xb01", "player set video surface when surface destroy.");
                                        }

                                        player.play();
                                        Log.d("xb01", "player play when surface destroy.");
                                    }
                                }

                                if(publisher != null && ParterResumer.this.mPublishSurfaceHolder != null) {
                                    publisher.resume(ParterResumer.this.mPublishSurfaceHolder.getSurface());
                                    Log.d("xb01", "publish resume.");
                                }
                            }
                        } else {
                            Log.d("xb01", "");
                        }

                    }
                };
                Log.d("xb0105", "add callback");
                if(this.mPublishSurfaceCallback != null) {
                    this.mPublishSurfaceCallback.setResumeCallback(callback);
                }

                this.mHostPlayerSurfaceCallback.setResumeCallback(callback);
                Iterator i$ = this.mPlayerSurfaceViewMap.values().iterator();

                while(i$.hasNext()) {
                    SurfaceCallback surfaceCallback = (SurfaceCallback)i$.next();
                    surfaceCallback.setResumeCallback(callback);
                }
            }

        }
    }

    public void release() {
        this.clearPlayerSurfaceView();
        if(this.mPublishSurfaceCallback != null) {
            this.mPublishSurfaceCallback.release();
            this.mPublishSurfaceCallback = null;
        }

        if(this.mHostPlayerSurfaceCallback != null) {
            this.mHostPlayerSurfaceCallback.release();
            this.mHostPlayerSurfaceCallback = null;
        }

        this.mHostPlayerSurfaceHolder = null;
        this.mPublishSurfaceHolder = null;
    }
}
