package com.example.websocketdemo.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import static java.time.LocalDateTime.now;

@Entity
public class ChatRoom {
    private static final int VALID_HOUR = 3;

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private int count;

    @Column
    private LocalDateTime expiryDate;

    protected ChatRoom() {
    }

    public ChatRoom(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.count = 0;
        this.expiryDate = now().plusHours(VALID_HOUR);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", expiryDate=" + expiryDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void enter() {
        this.count++;
    }

    public void exit() {
        this.count--;
        if (count < 0) {
            count = 0;
        }
    }

    public boolean isRemovable() {
        if (this.expiryDate.isAfter(now()) && this.count == 0) {
            return false;
        }
        return true;
    }
}
