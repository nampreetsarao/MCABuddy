package com.example.adminibm.mcabuddy.bean;

/**
 * Created by nampreet on 5/2/2016.
 */
public class CreateMessage {

    private Requester requester;
    private Message message;

    public CreateMessage(Requester requester, Message message) {
        this.requester = requester;
        this.message = message;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
