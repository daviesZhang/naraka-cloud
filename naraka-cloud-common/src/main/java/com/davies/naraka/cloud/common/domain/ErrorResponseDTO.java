package com.davies.naraka.cloud.common.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/19 2:25 PM
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorResponseDTO {

    private LocalDateTime timestamp;


    private String message;

    private Integer code;

    private Object data;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String message) {
        this();
        this.message = message;
    }

    public ErrorResponseDTO(String message, Object data) {
        this(message);
        this.data = data;
    }

}
