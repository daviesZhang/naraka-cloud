package com.davies.naraka.puppeteer.domain.dto;

import com.davies.naraka.puppeteer.annotation.QueryConfig;
import lombok.Data;


/**
 * @author davies
 */
@Data
public class CaseReportQueryDTO {


    @QueryConfig(alias = "c.project")
    private String project;
    @QueryConfig(alias = "c.name")
    private String name;
    @QueryConfig(alias = "c.createdBy")
    private String createdBy;
}
