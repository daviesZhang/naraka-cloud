package com.davies.naraka.puppeteer.domain.entity;

import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class ScriptCase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String project;


    private String environment;


    @Type(type = "ScriptStatus")
    private ScriptStatus scriptStatus;

    private String name;

    @OneToMany(targetEntity = CaseReport.class, mappedBy = "scriptCase")
    private List<CaseReport> caseReports;

    @OneToMany(targetEntity = CaseStep.class, mappedBy = "scriptCase")
    private List<CaseStep> steps;


    private String remark;


    @CreatedBy
    private String createdBy;
    @CreatedDate
    private LocalDateTime createdTime;

    private String updatedBy;
    @LastModifiedDate
    private LocalDateTime updatedTime;
}
