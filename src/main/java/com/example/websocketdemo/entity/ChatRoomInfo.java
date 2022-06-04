package com.example.websocketdemo.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatRoomInfo {

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private long count;

    @Column
    private LocalDateTime expiryDate;

    protected ChatRoomInfo() {
    }

    public ChatRoomInfo(String id, String name, long count, LocalDateTime expiryDate){
        this.id = id;
        this.name = name;
        this.count = count;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
