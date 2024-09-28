package com.example.demo.model.responses;

public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public BaseResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public BaseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
