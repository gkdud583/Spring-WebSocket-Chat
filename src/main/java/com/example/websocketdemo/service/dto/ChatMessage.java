package com.example.websocketdemo.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChatMessage {
    @NotNull(message = "메시지 타입은 필수 값입니다.")
    private MessageType type;

    @NotBlank(message = "내용은 필수 값입니다.")
    private String content;

    @NotBlank(message = "송신자는 필수 값입니다.")
    private String sender;

    public enum MessageType {
        CHAT, JOIN, LEAVE, ERROR,
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
          '}';
    }
}