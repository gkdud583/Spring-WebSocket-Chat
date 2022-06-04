package com.example.websocketdemo.model;


public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String email;


    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        ERROR,

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}