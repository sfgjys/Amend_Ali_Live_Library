//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.view.SurfaceView;

public interface MediaCapture {
  void setPreviewView(SurfaceView var1);

  void setPreviewSize(int var1, int var2);

  void startPreview();

  void stopPreview();

  void startCapture();

  void stopCapture();
}
