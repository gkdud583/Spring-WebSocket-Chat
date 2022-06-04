package com.example.websocketdemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;


public class ResponseToken {
    private String accessToken;

    @JsonFormat(timezone = "Asia/Seoul")
    private Date expiryDate;

    public ResponseToken(String accessToken, Date expiryDate) {
        this.accessToken = accessToken;
        this.expiryDate = expiryDate;
    }
}
