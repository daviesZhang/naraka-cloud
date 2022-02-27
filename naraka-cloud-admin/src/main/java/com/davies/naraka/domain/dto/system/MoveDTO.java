package com.davies.naraka.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**从from移动到to 下
 * @author davies
 * @date 2022/2/10 4:08 PM
 */
@Data
public class MoveDTO {

    @NotNull
    private Integer from;

    @NotNull
    private Integer to;
}
