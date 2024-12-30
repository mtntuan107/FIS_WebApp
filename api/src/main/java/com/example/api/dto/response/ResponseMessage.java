package com.example.api.dto.response;

public class ResponseMessage {
    private String message;
    private String status;

    public ResponseMessage(String s, String ok) {
        this.message = s;
        this.status = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

