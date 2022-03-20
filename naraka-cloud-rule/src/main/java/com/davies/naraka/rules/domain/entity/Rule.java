package com.davies.naraka.rules.domain.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author davies
 * @date 2022/3/17 19:29
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_rule")
@Accessors(chain = true)
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(nullable = false)
    private String project;

    @Column(nullable = false)
    private String name;


    @Column(columnDefinition = "text")
    private String content;


    private String remark;


    @CreatedBy
    private String createdBy;
    @CreatedDate
    private LocalDateTime createdTime;

    private String updatedBy;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Rule rule = (Rule) o;
        return id != null && Objects.equals(id, rule.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
