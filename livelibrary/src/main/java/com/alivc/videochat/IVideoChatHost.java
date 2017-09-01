//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat;

import android.content.Context;
import android.view.SurfaceView;

import java.util.List;
import java.util.Map;

public interface IVideoChatHost extends IVideoChat {
  int init(Context var1);

  int prepareToPublish(SurfaceView var1, int var2, int var3, Map<String, String> var4);

  int startToPublish(String var1);

  int stopPublishing();

  int finishPublishing();

  int release();

  void setErrorListener(IVideoChatHost.OnErrorListener var1);

  void setInfoListener(IVideoChatHost.OnInfoListener var1);

  void changeEarPhoneWhenChat(boolean var1);

  int abortChat();

  int pause();

  int resume();

  int launchChat(String var1, SurfaceView var2);

  int reconnectChat();

  int launchChats(Map<String, SurfaceView> var1);

  int addChats(Map<String, SurfaceView> var1);

  int removeChats(List<String> var1);

  int reconnectChat(String var1);

  public interface OnInfoListener {
    boolean onInfo(IVideoChatHost var1, int var2, String var3);
  }

  public interface OnErrorListener {
    boolean onError(IVideoChatHost var1, int var2, String var3);
  }
}
