package com.davies.naraka.system.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_role")
public class SysRole implements Serializable {
    private static final long serialVersionUID = -1084181793580760342L;
    @Id
    @Column(name = "code", nullable = false, length = 64)
    private String id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;


    @ManyToMany
    @JoinTable(name = "sys_role_authority", joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"),
            foreignKey = @ForeignKey(NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(NO_CONSTRAINT))
    private List<SysAuthority> authorities;

    @Column(name = "created_by", nullable = false, length = 64)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "updated_by", length = 64)
    @LastModifiedBy
    private String updatedBy;

}
