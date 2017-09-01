//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

import android.content.Context;
import android.view.Surface;

import java.util.Map;

public interface AlivcMediaRecorder {
  String ALIVC_FILTER_PARAM_BEAUTY_ON = "alivc_filter_param_beauty_on";

  void init(Context var1);

  /** @deprecated */
  @Deprecated
  void prepare(Map<String, Object> var1, Surface var2);

  void startRecord(String var1);

  int switchCamera();

  void stopRecord();

  void reset();

  /** @deprecated */
  @Deprecated
  void focusing(float var1, float var2);

  void autoFocus(float var1, float var2);

  boolean isFlagSupported(int var1);

  void setZoom(float var1);

  void setPreviewSize(int var1, int var2);

  void addFlag(int var1);

  void removeFlag(int var1);

  void release();

  void setFilterParam(Map<String, String> var1);

  void setOnRecordErrorListener(OnLiveRecordErrorListener var1);

  void setOnRecordStatusListener(OnRecordStatusListener var1);

  void setOnNetworkStatusListener(OnNetworkStatusListener var1);

  AlivcRecordReporter getRecordReporter();

  void pause();

  void resume(Surface var1);

  void resetVideoPusher();
}
