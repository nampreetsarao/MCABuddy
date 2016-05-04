package com.example.adminibm.mcabuddy.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nampreet on 4/28/2016.
 */
public class Message {

    private String uuid;
    private String title;
    private String message;
    private int likes;
    private String threadId;
    private String date;
    private List<String> tags = new ArrayList<>();
    private String channel;
    private String author;

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
