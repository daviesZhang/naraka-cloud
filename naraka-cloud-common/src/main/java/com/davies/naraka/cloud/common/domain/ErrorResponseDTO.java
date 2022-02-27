package com.davies.naraka.cloud.common.domain;



import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/19 2:25 PM
 */

public class ErrorResponseDTO {

    private LocalDateTime timestamp;


    private String message;


    private Object data;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    public ErrorResponseDTO(String message) {
        this();
        this.message = message;
    }
    public ErrorResponseDTO(String message,Object data) {
        this(message);
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
