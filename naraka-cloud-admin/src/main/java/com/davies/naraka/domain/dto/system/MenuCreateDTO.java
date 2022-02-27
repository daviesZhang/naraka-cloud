package com.davies.naraka.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/10 4:28 PM
 */
@Data
public class MenuCreateDTO {

    /**
     * URL
     */
    @NotBlank
    private String url;

    /**
     * CODE标识
     */
    @NotBlank
    private String code;

    private String remark;

    @NotNull
    @Min(value = 0)
    private Integer parent;
}
