package com.example.casttvandroiddemo.bean;

public class CastVideoBean {
    private String videoImageUrl;
    private String videoTitle;
    private String videoRealUrl;
    private String videoFirstUrl;

    public CastVideoBean(String videoImageUrl, String videoName, String videoRealUrl, String videoFirstUrl) {
        this.videoImageUrl = videoImageUrl;
        this.videoTitle = videoName;
        this.videoRealUrl = videoRealUrl;
        this.videoFirstUrl = videoFirstUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public void setVideoTitle(String videoName) {
        this.videoTitle = videoName;
    }

    public void setVideoRealUrl(String videoRealUrl) {
        this.videoRealUrl = videoRealUrl;
    }

    public void setVideoFirstUrl(String videoFirstUrl) {
        this.videoFirstUrl = videoFirstUrl;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoRealUrl() {
        return videoRealUrl;
    }

    public String getVideoFirstUrl() {
        return videoFirstUrl;
    }
}
