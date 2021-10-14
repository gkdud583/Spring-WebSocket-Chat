package com.example.websocketdemo.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
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

    @Builder
    public ChatRoomInfo(String id, String name, long count, LocalDateTime expiryDate){
        this.id = id;
        this.name = name;
        this.count = count;
        this.expiryDate = expiryDate;
    }






}
