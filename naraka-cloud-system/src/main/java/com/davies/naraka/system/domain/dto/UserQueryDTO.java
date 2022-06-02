package com.davies.naraka.system.domain.dto;

import com.davies.naraka.autoconfigure.domain.QueryField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author davies
 * @date 2022/5/31 09:11
 */
@Data
public class UserQueryDTO {

    private String username;


    private List<QueryField<LocalDateTime>> createdDate;


}
