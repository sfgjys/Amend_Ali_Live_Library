//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Process;

import com.alivc.videochat.utils.LogUtil;

public class AudioSource {
    private static final String TAG = "Audio";
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int FRAME_LENGTH = 512;
    private static final int IN_CHANNELS = 12;
    private int mFrameSize = 12288;
    private int mSampleRateInHz = 32000;
    private int mChannelConfig = 12;
    private AudioRecord mAudioRecord = null;
    private AudioSource.AudioThread mThread = null;
    private byte[] mMuteData = null;
    private boolean mMute;
    private Status mStatus;
    private AudioSource.AudioSourceListener mAudioSourceListener = null;

    public AudioSource() {
        this.mStatus = Status.STOPED;
        this.mMute = false;
    }

    public void setAudioSourceListener(AudioSource.AudioSourceListener listener) {
        this.mAudioSourceListener = listener;
    }

    public void setRecordParams(int channelConfig, int sampleRateInHz, int frameSize) {
        this.mSampleRateInHz = sampleRateInHz;
        this.mChannelConfig = channelConfig;
        this.mFrameSize = frameSize;
    }

    public void start() throws Exception {
        if(this.mStatus == Status.STOPED) {
            int minRecordBufSize;
            if(this.mMuteData == null) {
                this.mMuteData = new byte[this.mFrameSize];

                for(minRecordBufSize = 0; minRecordBufSize < this.mFrameSize; ++minRecordBufSize) {
                    this.mMuteData[minRecordBufSize] = 0;
                }
            }

            minRecordBufSize = AudioRecord.getMinBufferSize(this.mSampleRateInHz, 12, 2);
            if(minRecordBufSize < 2048) {
                minRecordBufSize = 2048;
            }

            this.mAudioRecord = new AudioRecord(1, this.mSampleRateInHz, this.mChannelConfig, 2, minRecordBufSize * 10);
            this.mAudioRecord.startRecording();
            short[] not_used = new short[this.mFrameSize];
            int ret = this.mAudioRecord.read(not_used, 0, this.mFrameSize);
            if(ret != -3 && ret != -2) {
                this.mStatus = Status.RUNNING;
                if(this.mThread == null) {
                    this.mThread = new AudioSource.AudioThread();
                    this.mThread.start();
                }

            } else {
                throw new PublisherException("audio record read fail");
            }
        }
    }

    public void pause() {
        if(this.mStatus == Status.RUNNING) {
            this.mStatus = Status.PAUSED;
        }

    }

    public void resume() {
        if(this.mStatus == Status.PAUSED) {
            this.mStatus = Status.RUNNING;
        }

    }

    public Status getStatus() {
        return this.mStatus;
    }

    public void setMute(boolean flag) {
        this.mMute = flag;
    }

    public void stop() {
        if(this.mStatus != Status.STOPED) {
            if(this.mThread != null) {
                this.mThread.terminate();
                this.mThread = null;
            }

            if(this.mAudioRecord != null) {
                this.mAudioRecord.stop();
                this.mAudioRecord.release();
                this.mAudioRecord = null;
            }
        }

    }

    @SuppressLint({"NewApi"})
    private static boolean isAECAailable() {
        return AcousticEchoCanceler.isAvailable();
    }

    @SuppressLint({"NewApi"})
    private static boolean isNSAvailable() {
        return NoiseSuppressor.isAvailable();
    }

    class AudioThread extends Thread {
        private volatile boolean running = true;

        public void terminate() {
            this.running = false;
        }

        AudioThread() {
        }

        public void run() {
            try {
                Process.setThreadPriority(-19);
            } catch (Exception var3) {
                LogUtil.e("Audio", "Set record thread priority failed: " + var3.getMessage());
            }

            byte[] data = new byte[AudioSource.this.mFrameSize];

            while(this.running) {
                int ret = AudioSource.this.mAudioRecord.read(data, 0, AudioSource.this.mFrameSize);
                if(ret == -3 || ret == -2) {
                    break;
                }

                if(AudioSource.this.getStatus() == Status.RUNNING) {
                    if(AudioSource.this.mMute) {
                        if(AudioSource.this.mAudioSourceListener != null) {
                            AudioSource.this.mAudioSourceListener.onAudioFrame(AudioSource.this.mMuteData, ret);
                        }
                    } else if(AudioSource.this.mAudioSourceListener != null) {
                        AudioSource.this.mAudioSourceListener.onAudioFrame(data, ret);
                    }
                }
            }

            data = (byte[])null;
            this.running = false;
        }
    }

    public interface AudioSourceListener {
        void onAudioFrame(byte[] var1, int var2);
    }
}
