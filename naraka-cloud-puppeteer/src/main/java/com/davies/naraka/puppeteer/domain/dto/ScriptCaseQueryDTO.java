package com.davies.naraka.puppeteer.domain.dto;

import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 *
 * @author davies
 */
@Data
public class ScriptCaseQueryDTO {

    @NotNull
    private QueryField<@NotBlank String> project;
    @NotNull
    private QueryField<ScriptStatus> scriptStatus;
}
