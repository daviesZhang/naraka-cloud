package com.davies.naraka.system.domain.entity;

import com.davies.naraka.system.domain.enums.AuthorityType;
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

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_authority")
public class SysAuthority implements Serializable {
    private static final long serialVersionUID = 7982629678328307509L;

    @Id
    @GeneratedValue(generator = "id_generator")
    @GenericGenerator(name = "id_generator", strategy = "com.davies.naraka.system.config.IdGenerator")
    @Column(name = "id", nullable = false, length = 64)
    private String id;


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "resource", nullable = false)
    private String resource;

    @Column(name = "resource_type", nullable = false)
    @Type(type = "AuthorityType")
    private AuthorityType resourceType;

    @Lob
    @Basic(fetch = LAZY)
    @Column(name = "data")
    private String data;

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
