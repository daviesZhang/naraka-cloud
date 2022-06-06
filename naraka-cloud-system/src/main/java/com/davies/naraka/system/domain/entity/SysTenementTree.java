package com.davies.naraka.system.domain.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sys_tenement_tree")
public class SysTenementTree implements Serializable {
    private static final long serialVersionUID = 2690295626168828624L;
    @EmbeddedId
    private SysTenementTreeId id;


}
