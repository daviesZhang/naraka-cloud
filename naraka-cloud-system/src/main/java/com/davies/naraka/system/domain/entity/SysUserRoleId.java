package com.davies.naraka.system.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Embeddable
public class SysUserRoleId implements Serializable {
    private static final long serialVersionUID = -8424504326767648409L;
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "role_code", nullable = false, length = 64)
    private String roleCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysUserRoleId entity = (SysUserRoleId) o;
        return Objects.equals(this.roleCode, entity.roleCode) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleCode, userId);
    }

}
