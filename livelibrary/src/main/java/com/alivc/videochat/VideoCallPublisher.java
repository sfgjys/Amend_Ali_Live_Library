//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.app.Activity;
import android.view.Surface;
import android.view.SurfaceView;

import com.alivc.videochat.publisher.AlivcSurfaceView;

public interface VideoCallPublisher {
  int initPublisher(Activity var1);

  int preparePublisher(AlivcSurfaceView var1, int var2, int var3, int var4);

  int preparePublisher(Surface var1, int var2, int var3, int var4);

  int startPublisher(String var1);

  int stopPublisher();

  int releasePublisher();

  int startVideoCall(SurfaceView var1, String var2);

  int stopVideoCall();

  String getDebugInfos();

  void switchCamera();
}
