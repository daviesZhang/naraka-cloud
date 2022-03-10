package com.davies.naraka.puppeteer.domain.dto;

import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import lombok.Data;

/**
 * @author davies
 */
@Data
public class ScriptCaseQueryDTO {

    private QueryField<String> project;

    private QueryField<ScriptStatus> scriptStatus;
}
