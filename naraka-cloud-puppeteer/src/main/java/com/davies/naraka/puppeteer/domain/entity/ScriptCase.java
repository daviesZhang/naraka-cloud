package com.davies.naraka.puppeteer.domain.entity;

import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author sycho
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ScriptCase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String project;


    private String environment;


    private String name;

    @OneToMany(targetEntity = CaseReport.class,mappedBy = "scriptCase")
    private List<CaseReport> caseReports;

    @OneToMany(targetEntity = CaseStep.class,mappedBy = "scriptCase")
    private List<CaseStep> steps;


    private String remark;


    private String createdBy;
    @CreatedDate
    private LocalDateTime createdTime;

    private String updatedBy;
    @LastModifiedDate
    private LocalDateTime updatedTime;
}
