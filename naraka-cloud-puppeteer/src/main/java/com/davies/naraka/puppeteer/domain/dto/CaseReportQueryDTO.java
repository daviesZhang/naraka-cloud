package com.davies.naraka.puppeteer.domain.dto;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import lombok.Data;


/**
 * @author davies
 */
@Data
public class CaseReportQueryDTO {


    private String project;

    @ColumnName("c.name")
    private String name;
    @ColumnName("c.createdBy")
    private String createdBy;
}
