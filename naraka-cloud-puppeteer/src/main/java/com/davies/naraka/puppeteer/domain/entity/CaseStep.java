package com.davies.naraka.puppeteer.domain.entity;


import com.davies.naraka.puppeteer.domain.enums.StepAction;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @author sycho
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class CaseStep {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(targetEntity = ScriptCase.class)
    @JoinColumn(referencedColumnName = "id",foreignKey = @ForeignKey(NO_CONSTRAINT))
    private ScriptCase scriptCase;


    @Column(nullable = false)
    private String name;


    @Type(type = "StepAction")
    private StepAction action;



    private String remark;


    private String createdBy;
    @CreatedDate
    private LocalDateTime createdTime;

    private String updatedBy;
    @LastModifiedDate
    private LocalDateTime updatedTime;


}
