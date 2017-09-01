//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

public interface OnNetworkStatusListener {
  void onNetworkBusy();

  void onNetworkFree();

  void onConnectionStatusChange(int var1);

  boolean onNetworkReconnect();
}
