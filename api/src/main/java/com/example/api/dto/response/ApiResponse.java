package com.example.api.dto.response;

public class ApiResponse{
    private String message;

    public ApiResponse(String message) {
        this.message = message;
    }

    // Getter và setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
