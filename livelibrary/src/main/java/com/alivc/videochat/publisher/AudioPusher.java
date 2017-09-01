//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.content.Context;
import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alivc.videochat.utils.LogUtil;

public class AudioPusher {
    private static final String TAG = "AudioPusher0927";
    private int minBufferSize;
    private AudioRecord audioRecord;
    private int mSampleRateInHz = 32000;
    private AcousticEchoCanceler mAEC;
    private boolean mPusherRuning = false;
    private AudioPusher.AudioSourceListener mAudioSourceListener = null;
    private Status mStatus;
    private boolean mMute;
    private Context mContext;
    private byte[] mMuteData = null;
    private int mFrameSize;

    public AudioPusher(Context context) {
        LogUtil.d("AudioPusher0927", "new AudioPusher.");
        this.mStatus = Status.STOPED;
        this.mMute = false;
        this.mContext = context;
    }

    public void setRecordParams(int channelConfig, int sampleRateInHz, int frameSize) {
        this.mSampleRateInHz = sampleRateInHz;
        this.mFrameSize = frameSize;
    }

    public void start() throws Exception {
        LogUtil.d("AudioPusher0927", "start.");
        if (this.mStatus == Status.STOPED) {
            if (this.mMuteData == null) {
                this.mMuteData = new byte[this.mFrameSize];

                for (int i = 0; i < this.mFrameSize; ++i) {
                    this.mMuteData[i] = 0;
                }
            }

            this.minBufferSize = AudioRecord.getMinBufferSize(this.mSampleRateInHz, 12, 2);
            if (this.minBufferSize < this.mFrameSize) {
                this.minBufferSize = this.mFrameSize;
            }

            this.audioRecord = new AudioRecord(1, this.mSampleRateInHz, 12, 2, this.minBufferSize * 10);
            this.mStatus = Status.RUNNING;
            this.mPusherRuning = true;
            if (this.audioRecord.getRecordingState() == 1) {
                this.audioRecord.startRecording();
                LogUtil.d("AudioPusher0927", "new thread and start thread. " + this.audioRecord.getRecordingState());
                if (this.audioRecord.getRecordingState() == 1) {
                    throw new PublisherException("audio record read fail");
                }

                (new Thread(new AudioPusher.AudioRecordTask())).start();
            }

        }
    }

    public void pause() {
        LogUtil.d("AudioPusher0927", "pause.");
        if (this.mStatus == Status.RUNNING) {
            this.mStatus = Status.PAUSED;
        }

    }

    public void resume() {
        Log.d("AudioPusher0927", "resume.");
        if (this.mStatus == Status.PAUSED) {
            this.mStatus = Status.RUNNING;
        }

    }

    public void setMute(boolean flag) {
        this.mMute = flag;
    }

    public void stop() {
        Log.d("AudioPusher0927", "stop.");
        if (null != this.audioRecord) {
            if (this.mStatus != Status.STOPED) {
                this.mStatus = Status.STOPED;
                this.mPusherRuning = false;
                if (this.audioRecord.getRecordingState() == 3) {
                    this.audioRecord.stop();
                }
            }

            if (this.mAEC != null) {
                this.mAEC.setEnabled(false);
                this.mAEC.release();
                this.mAEC = null;
            }

        }
    }

    public void release() {
        Log.d("AudioPusher0927", "release.");
        if (null != this.audioRecord) {
            this.mPusherRuning = false;
            if (this.audioRecord.getRecordingState() == 1) {
                this.audioRecord.release();
            }

            this.audioRecord = null;
        }
    }

    public void setAudioSourceListener(AudioPusher.AudioSourceListener listener) {
        this.mAudioSourceListener = listener;
    }

    private boolean isTelephonyCalling() {
        boolean calling = false;
        if (this.mContext != null) {
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (2 == telephonyManager.getCallState() || 1 == telephonyManager.getCallState()) {
                calling = true;
            }
        }

        return calling;
    }

    class AudioRecordTask implements Runnable {
        AudioRecordTask() {
        }

        public void run() {
            Log.d("AudioPusher0927", "run begin ." + AudioPusher.this.mPusherRuning + " " + AudioPusher.this.audioRecord.getRecordingState());

            try {
                Process.setThreadPriority(-19);
            } catch (Exception var8) {
                Log.e("AudioPusher0927", "Set record thread priority failed: " + var8.getMessage());
            }

            byte[] buffer = new byte[AudioPusher.this.mFrameSize];
            long time = System.currentTimeMillis();
            long startTime = time;
            int count = 0;

            while (AudioPusher.this.mPusherRuning && AudioPusher.this.audioRecord.getRecordingState() == 3) {
                int len = AudioPusher.this.audioRecord.read(buffer, 0, buffer.length);
                ++count;
                Log.d("AudioPusher0927", "audio: on audio " + len + " time : " + (System.currentTimeMillis() - time) + "average time : " + (System.currentTimeMillis() - startTime) / (long) count);
                time = System.currentTimeMillis();
                if (0 < len && (AudioPusher.this.mStatus != Status.PAUSED || !AudioPusher.this.isTelephonyCalling())) {
                    if (AudioPusher.this.mMute) {
                        if (AudioPusher.this.mAudioSourceListener != null) {
                            AudioPusher.this.mAudioSourceListener.onAudioFrame(AudioPusher.this.mMuteData, AudioPusher.this.mFrameSize);
                        }
                    } else if (AudioPusher.this.mAudioSourceListener != null) {
                        System.out.println("9527LEN: " + len);
                        AudioPusher.this.mAudioSourceListener.onAudioFrame(AudioPusher.this.mMuteData, AudioPusher.this.mFrameSize);
                    }
                }
            }

            Log.e("AudioPusher0927", "exist debug.");
        }
    }

    public interface AudioSourceListener {
        void onAudioFrame(byte[] var1, int var2);
    }
}
