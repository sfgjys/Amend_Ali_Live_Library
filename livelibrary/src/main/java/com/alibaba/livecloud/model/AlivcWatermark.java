//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.livecloud.model;

public class AlivcWatermark {
    public static final int SITE_TOP_RIGHT = 1;
    public static final int SITE_TOP_LEFT = 2;
    public static final int SITE_BOTTOM_RIGHT = 3;
    public static final int SITE_BOTTOM_LEFT = 4;
    private String mWatermarkUrl;
    private int mPaddingX;
    private int mPaddingY;
    private int mSite;

    public AlivcWatermark() {
    }

    public String getWatermarkUrl() {
        return this.mWatermarkUrl;
    }

    public void setWatermarkUrl(String watermarkUrl) {
        this.mWatermarkUrl = watermarkUrl;
    }

    public int getPaddingX() {
        return this.mPaddingX;
    }

    public void setPaddingX(int paddingX) {
        this.mPaddingX = paddingX;
    }

    public int getPaddingY() {
        return this.mPaddingY;
    }

    public void setPaddingY(int paddingY) {
        this.mPaddingY = paddingY;
    }

    public int getSite() {
        return this.mSite;
    }

    public void setSite(int site) {
        this.mSite = site;
    }

    public static class Builder {
        String watermarkUrl;
        int paddingX;
        int paddingY;
        int site;

        public Builder() {
        }

        public AlivcWatermark build() {
            AlivcWatermark watermark = new AlivcWatermark();
            watermark.mWatermarkUrl = this.watermarkUrl;
            watermark.mPaddingX = this.paddingX;
            watermark.mPaddingY = this.paddingY;
            watermark.mSite = this.site;
            return watermark;
        }

        public AlivcWatermark.Builder watermarkUrl(String watermarkUrl) {
            this.watermarkUrl = watermarkUrl;
            return this;
        }

        public AlivcWatermark.Builder paddingX(int paddingX) {
            this.paddingX = paddingX;
            return this;
        }

        public AlivcWatermark.Builder paddingY(int paddingY) {
            this.paddingY = paddingY;
            return this;
        }

        public AlivcWatermark.Builder site(int site) {
            this.site = site;
            return this;
        }
    }
}
