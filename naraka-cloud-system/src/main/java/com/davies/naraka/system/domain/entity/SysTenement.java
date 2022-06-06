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
@Table(name = "sys_tenement")
public class SysTenement implements Serializable {
    private static final long serialVersionUID = -5500911615012024438L;
    @Id
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "`desc`")
    private String desc;

    @Column(name = "parent_code", length = 64)
    private String parentId;

    @ManyToMany(mappedBy = "tenements", fetch = FetchType.LAZY)
    private List<SysUser> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_tenement_role",
            joinColumns = @JoinColumn(name = "tenement_id"),
            inverseJoinColumns = @JoinColumn(name = "role_code"),
            foreignKey = @ForeignKey(NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(NO_CONSTRAINT))
    private List<SysRole> roles;


    @Column(name = "logic_delete", nullable = false)
    private Boolean logicDelete = false;

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
