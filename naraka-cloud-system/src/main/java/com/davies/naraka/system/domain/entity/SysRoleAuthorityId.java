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
public class SysRoleAuthorityId implements Serializable {
    private static final long serialVersionUID = 3516998581556946217L;
    @Column(name = "role_code", nullable = false, length = 64)
    private String roleCode;

    @Column(name = "authority_id", nullable = false, length = 64)
    private String authorityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysRoleAuthorityId entity = (SysRoleAuthorityId) o;
        return Objects.equals(this.authorityId, entity.authorityId) &&
                Objects.equals(this.roleCode, entity.roleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId, roleCode);
    }

}
