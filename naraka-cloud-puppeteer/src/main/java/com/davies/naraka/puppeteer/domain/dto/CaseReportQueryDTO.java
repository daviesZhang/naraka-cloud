package com.davies.naraka.puppeteer.domain.dto;

import com.davies.naraka.puppeteer.annotation.QueryParams;
import lombok.Data;


/**
 * @author davies
 */
@Data
public class CaseReportQueryDTO {


    @QueryParams(alias = "c.project")
    private String project;
    @QueryParams(alias = "c.name")
    private String name;
    @QueryParams(alias = "c.createdBy")
    private String createdBy;
}
