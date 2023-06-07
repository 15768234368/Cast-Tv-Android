package com.example.casttvandroiddemo.bean;

public class InternetHistoryBean {

    private long id;
    private String title;
    private String url;
    private String timestamp;

    public InternetHistoryBean(long id, String title, String url, String timestamp) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.timestamp = timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
