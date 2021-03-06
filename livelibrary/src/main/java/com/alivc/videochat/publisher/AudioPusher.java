//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alivc.videochat.publisher;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Environment;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alivc.videochat.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

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
    // -----------------------------------------------以下是修改sdk的部分---------------------------------------------------------
    private AudioTrack mPlayPCMTool;
    private LinkedList<byte[]> mListByte;
    private byte[] mPlayByte;
    private boolean mThreadRoutineIsContinue;
    // --------------------------------------------------------------------------------------------------------

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

            // 参数一为MIC 参数二由setRecordParams方法设定（默认32000） 参数三双声道 参数四
            this.audioRecord = new AudioRecord(1, this.mSampleRateInHz, 12, 2, this.minBufferSize * 10);

            // -------------------------------------------------以下是修改sdk的部分-------------------------------------------------------
            // AudioTrack 得到播放最小缓冲区的大小
            int mPlayMinBufferSize = AudioTrack.getMinBufferSize(this.mSampleRateInHz, 12, 2);
            // 实例化播放音频对象
            mPlayPCMTool = new AudioTrack(AudioManager.STREAM_MUSIC, this.mSampleRateInHz, 12, 2, mPlayMinBufferSize,
                    AudioTrack.MODE_STREAM);

            // 实例化一个链表，用来存放AudioRecord采集的音频字节组数
            mListByte = new LinkedList<>();

            // 实例化一个长度为播放最小缓冲大小的字节数组
            mPlayByte = new byte[mPlayMinBufferSize];
            // --------------------------------------------------------------------------------------------------------

            this.mStatus = Status.RUNNING;
            this.mPusherRuning = true;
            if (this.audioRecord.getRecordingState() == 1) {
                this.audioRecord.startRecording();
                LogUtil.d("AudioPusher0927", "new thread and start thread. " + this.audioRecord.getRecordingState());
                if (this.audioRecord.getRecordingState() == 1) {
                    throw new PublisherException("audio record read fail");
                }

                (new Thread(new AudioPusher.AudioRecordTask())).start();
                // -----------------------------------------------以下是修改sdk的部分---------------------------------------------------------
                mThreadRoutineIsContinue = true;// 确定播放混音的线程开启了
                (new Thread(new PlayPCM())).start();// 开启播放混音后的线程
                // --------------------------------------------------------------------------------------------------------
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

                    // --------------------------------------------------以下是修改sdk的部分------------------------------------------------------
                    // 确定线程关闭
                    mThreadRoutineIsContinue = false;
                    // --------------------------------------------------------------------------------------------------------
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
            Log.d("AudioPusher0927", "run begin ." + AudioPusher.this.mPusherRuning + " " +
                    AudioPusher.this.audioRecord.getRecordingState());

            try {
                Process.setThreadPriority(-19);
            } catch (Exception var8) {
                Log.e("AudioPusher0927", "Set record thread priority failed: " + var8.getMessage());
            }

            // -------------------------------------------------以下是修改sdk的部分-------------------------------------------------------
            FileInputStream fileInputStreamFromFile = getFileInputStreamFromFile(Environment.getExternalStorageDirectory() +
                    "/Decode/123456.pcm");
            byte[] pcmBuffer = new byte[AudioPusher.this.mFrameSize];
            byte[] collectMiddleBytes;
            // --------------------------------------------------------------------------------------------------------

            byte[] buffer = new byte[AudioPusher.this.mFrameSize];
            long time = System.currentTimeMillis();
            long startTime = time;
            int count = 0;

            while (AudioPusher.this.mPusherRuning && AudioPusher.this.audioRecord.getRecordingState() == 3) {
                try {
                    int len = AudioPusher.this.audioRecord.read(buffer, 0, buffer.length);
                    // --------------------------------------------以下是修改sdk的部分------------------------------------------------------------
                    int read = fileInputStreamFromFile.read(pcmBuffer);// 读取背景音乐的PCM数据
                    // --------------------------------------------------------------------------------------------------------
                    ++count;
                    Log.d("AudioPusher0927", "audio: on audio " + len + " time : " + (System.currentTimeMillis() - time) +
                            "average time : " + (System.currentTimeMillis() - startTime) / (long) count);
                    time = System.currentTimeMillis();
                    if (0 < len && (AudioPusher.this.mStatus != Status.PAUSED || !AudioPusher.this.isTelephonyCalling())) {
                        if (AudioPusher.this.mMute) {
                            if (AudioPusher.this.mAudioSourceListener != null) {
                                AudioPusher.this.mAudioSourceListener.onAudioFrame(AudioPusher.this.mMuteData,
                                        AudioPusher.this.mFrameSize);
                            }
                        } else if (AudioPusher.this.mAudioSourceListener != null) {
                            // -------------------------------------------------以下是修改sdk的部分-------------------------------------------------------
                            //  测试一
//                            if (read != -1) {
//                                System.out.println("9527LEN: " + len);
//                                AudioPusher.this.mAudioSourceListener.onAudioFrame(pcmBuffer, read);
//                            } else {
//                                AudioPusher.this.mAudioSourceListener.onAudioFrame(buffer, len);
//                            }
                            // 测试二
//                            for (int i = 0; i < buffer.length; i++) {
//                                buffer[i] = (byte) (buffer[i] * 1.8f);
//                            }
//                            AudioPusher.this.mAudioSourceListener.onAudioFrame(buffer, len);
                            // 测试三
                            if (read != -1) {
                                // 混音算法。两PCM个数据相加就行，其中的*是调节PCM数据的音量
                                for (int i = 0; i < buffer.length; i++) {
                                    buffer[i] = (byte) (pcmBuffer[i] * 0.9f + buffer[i] * 1.8f);
                                }
                            }
                            // 将混音数据克隆给PlayPCM线程进行播放实现人声入耳功能
                            collectMiddleBytes = buffer.clone();
                            if (mListByte.size() >= 2) {
                                mListByte.removeFirst();
                            }
                            mListByte.add(collectMiddleBytes);

                            // sdk传递数据出去，进行推流
                            AudioPusher.this.mAudioSourceListener.onAudioFrame(buffer, len);
                            // --------------------------------------------------------------------------------------------------------
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileInputStreamFromFile = null;

            Log.e("AudioPusher0927", "exist debug.");
        }
    }

    public interface AudioSourceListener {
        void onAudioFrame(byte[] var1, int var2);
    }

    // ---------------------------------------------------以下是修改sdk的部分-----------------------------------------------------
    private static FileInputStream getFileInputStreamFromFile(String fileUrl) {
        FileInputStream fileInputStream = null;

        try {
            File file = new File(fileUrl);

            fileInputStream = new FileInputStream(file);
        } catch (Exception e) {
            System.out.println("GetBufferedInputStreamFromFile异常");
        }

        return fileInputStream;
    }

    // 播放混音的PCM数据的线程
    private class PlayPCM implements Runnable {
        @Override
        public void run() {
            byte[] playMiddle;
            // 开始播放
            mPlayPCMTool.play();

            while (mThreadRoutineIsContinue) {
                try {
                    mPlayByte = mListByte.getFirst();
                    playMiddle = mPlayByte.clone();
                    mPlayPCMTool.write(playMiddle, 0, playMiddle.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // --------------------------------------------------------------------------------------------------------
}
