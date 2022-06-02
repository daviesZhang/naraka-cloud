package com.davies.naraka.system.domain.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table(name = "sys_user")
public class SysUser implements Serializable {
    private static final long serialVersionUID = 4305083131364728304L;
    @Id
    @GeneratedValue(generator = "id_generator")
    @GenericGenerator(name = "id_generator", strategy = "com.davies.naraka.system.config.IdGenerator")
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "email", length = 128)
    @Type(type = "crypto_email")
    private String email;

    @Column(name = "phone", length = 128)
    @Type(type = "crypto_phone")
    private String phone;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_code"),
            foreignKey = @ForeignKey(NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(NO_CONSTRAINT))
    private List<SysRole> roles = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_tenement",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tenement_id"),
            foreignKey = @ForeignKey(NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(NO_CONSTRAINT))
    private List<SysTenement> tenements = new ArrayList<>();

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
