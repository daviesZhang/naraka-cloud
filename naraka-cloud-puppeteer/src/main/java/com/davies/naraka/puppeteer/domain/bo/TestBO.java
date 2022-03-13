package com.davies.naraka.puppeteer.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/3/13 9:55 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestBO {

    private String name;

    private LocalDateTime updatedTime;


}
