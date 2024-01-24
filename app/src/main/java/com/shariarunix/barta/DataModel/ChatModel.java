package com.shariarunix.barta.DataModel;

import java.io.Serializable;

public class ChatModel {
    String chatId, chatMessage, userName;
    long msgTime;
    boolean isDeleted;

    public ChatModel() {
        // Default Constructor
    }

    public ChatModel(String chatId, String chatMessage, String userName, long msgTime, boolean isDeleted) {
        this.chatId = chatId;
        this.chatMessage = chatMessage;
        this.userName = userName;
        this.msgTime = msgTime;
        this.isDeleted = isDeleted;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
