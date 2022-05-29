package com.davies.naraka.system.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sys_authority")
public class SysAuthority implements Serializable {
    private static final long serialVersionUID = 7982629678328307509L;
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "authority_method", nullable = false)
    private Integer authorityMethod;

    @Lob
    @Column(name = "data")
    private String data;

    @Column(name = "created_by", nullable = false, length = 64)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

}
