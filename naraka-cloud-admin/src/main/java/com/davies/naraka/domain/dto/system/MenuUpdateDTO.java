package com.davies.naraka.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/10 4:37 PM
 */
@Data
public class MenuUpdateDTO {

    @NotNull
    private Integer id;

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


    /**
     * 备注
     */

    private String remark;
}
