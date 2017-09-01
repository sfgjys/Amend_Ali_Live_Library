//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

public interface AlivcRecordReporter {
  int VIDEO_CAPTURE_FPS = 1;
  int AUDIO_ENCODER_FPS = 2;
  int VIDEO_ENCODER_FPS = 3;
  int OUTPUT_BITRATE = 4;
  int AV_OUTPUT_DIFF = 5;
  int AUDIO_OUTPUT_FPS = 6;
  int VIDEO_OUTPUT_FPS = 7;
  int STREAM_SERVER_IP = 9;
  int VIDEO_DELAY_DURATION = 10;
  int AUDIO_DELAY_DURATION = 11;
  int VIDEO_CACHE_FRAME_CNT = 12;
  int AUDIO_CACHE_FRAME_CNT = 13;
  int VIDEO_CACHE_BYTE_SIZE = 14;
  int AUDIO_CACHE_BYTE_SIZE = 15;
  int VIDEO_FRAME_DISCARD_CNT = 16;
  int AUDIO_FRAME_DISCARD_CNT = 17;
  int CUR_VIDEO_BEAUTY_DURATION = 18;
  int CUR_VIDEO_ENCODER_DURATION = 19;
  int VIDEO_OUTPUT_FRAME_COUNT = 4103;
  int VIDEO_OUTPUT_DATA_SIZE = 4106;
  int VIDEO_BUFFER_COUNT = 4107;
  int AUDIO_OUTPUT_DATA_SIZE = 4108;
  int CURR_VIDEO_ENCODE_DATA_SIZE = 4110;
  int CUR_VIDEO_ENCODE_BITRATE = 20;
  int VIDEO_OUTPUT_DELAY = 4109;

  int getInt(int var1);

  double getDouble(int var1);

  long getLong(int var1);

  float getFloat(int var1);

  boolean getBoolean(int var1);

  String getString(int var1);

  Object getValue(int var1);
}
