//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.live;

public class AlivcMediaRecorderFactory {
    public AlivcMediaRecorderFactory() {
    }

    public static AlivcMediaRecorder createMediaRecorder() {
        return new AlivcMediaRecorderExt();
    }
}
