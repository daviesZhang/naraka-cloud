package com.davies.naraka.admin.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/19 2:25 PM
 */
@Data
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

}
