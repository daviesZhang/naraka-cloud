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
public class SysTenementRoleId implements Serializable {
    private static final long serialVersionUID = -7376218511216722880L;
    @Column(name = "tenement_id", nullable = false, length = 64)
    private String tenementId;

    @Column(name = "role_code", nullable = false, length = 64)
    private String roleCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysTenementRoleId entity = (SysTenementRoleId) o;
        return Objects.equals(this.tenementId, entity.tenementId) &&
                Objects.equals(this.roleCode, entity.roleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenementId, roleCode);
    }

}
