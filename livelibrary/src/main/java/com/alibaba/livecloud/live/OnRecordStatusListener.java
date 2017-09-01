//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

public interface OnRecordStatusListener {
  void onDeviceAttach();

  void onDeviceAttachFailed(int var1);

  void onSessionAttach();

  void onSessionDetach();

  void onDeviceDetach();

  void onIllegalOutputResolution();
}
