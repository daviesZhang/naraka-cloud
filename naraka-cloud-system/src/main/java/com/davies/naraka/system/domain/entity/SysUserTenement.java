package com.davies.naraka.system.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sys_user_tenement")
public class SysUserTenement implements Serializable {
    private static final long serialVersionUID = 5798751722373140498L;
    @EmbeddedId
    private SysUserTenementId id;


    @Column(name = "created_by", nullable = false, length = 64)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;


}
