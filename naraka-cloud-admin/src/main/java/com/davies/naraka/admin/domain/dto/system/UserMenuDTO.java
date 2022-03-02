package com.davies.naraka.admin.domain.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author davies
 * @date 2022/3/2 9:36 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMenuDTO {

    private String url;

    private Integer id;

    private Integer parent;
}
