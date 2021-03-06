package com.davies.naraka.puppeteer.domain.entity;

import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
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
@ToString
public class CaseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ColumnTransformer
    @ManyToOne(targetEntity = ScriptCase.class)
    @JoinColumn(referencedColumnName = "id",foreignKey = @ForeignKey(NO_CONSTRAINT))
    private ScriptCase scriptCase;


    private String project;


    private String environment;


    private String name;

    private String remark;

    private String createdBy;
    @CreatedDate
    private LocalDateTime createdTime;

    private String updatedBy;
    @LastModifiedDate
    private LocalDateTime updatedTime;
}
