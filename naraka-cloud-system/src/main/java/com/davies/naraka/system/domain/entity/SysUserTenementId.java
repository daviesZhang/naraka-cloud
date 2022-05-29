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
public class SysUserTenementId implements Serializable {
    private static final long serialVersionUID = 7304957719141874947L;
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "tenement_id", nullable = false, length = 64)
    private String tenementId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysUserTenementId entity = (SysUserTenementId) o;
        return Objects.equals(this.tenementId, entity.tenementId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenementId, userId);
    }

}
