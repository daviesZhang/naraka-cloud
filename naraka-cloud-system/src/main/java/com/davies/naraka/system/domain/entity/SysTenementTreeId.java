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
public class SysTenementTreeId implements Serializable {
    private static final long serialVersionUID = -1366538008965185301L;
    @Column(name = "descendant", nullable = false, length = 64)
    private String descendant;

    @Column(name = "ancestor", nullable = false, length = 64)
    private String ancestor;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysTenementTreeId entity = (SysTenementTreeId) o;
        return Objects.equals(this.distance, entity.distance) &&
                Objects.equals(this.ancestor, entity.ancestor) &&
                Objects.equals(this.descendant, entity.descendant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, ancestor, descendant);
    }

}
