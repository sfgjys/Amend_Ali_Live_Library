//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.content.Context;
import android.view.SurfaceView;

import java.util.List;
import java.util.Map;

public interface IVideoChatParter extends IVideoChat {
  int init(Context var1);

  int startToPlay(String var1, SurfaceView var2);

  int stopPlaying();

  int release();

  void setErrorListener(IVideoChatParter.OnErrorListener var1);

  void setInfoListener(IVideoChatParter.OnInfoListener var1);

  void changeEarPhoneWhenChat(boolean var1);

  int offlineChat();

  int pause();

  int resume();

  int reconnect();

  int onlineChat(String var1, int var2, int var3, SurfaceView var4, Map<String, String> var5, String var6);

  int onlineChats(String var1, int var2, int var3, SurfaceView var4, Map<String, String> var5, String var6, Map<String, SurfaceView> var7);

  int addChats(Map<String, SurfaceView> var1);

  int removeChats(List<String> var1);

  int reconnect(String var1);

  public interface OnInfoListener {
    boolean onInfo(IVideoChatParter var1, int var2, String var3);
  }

  public interface OnErrorListener {
    boolean onError(IVideoChatParter var1, int var2, String var3);
  }
}
