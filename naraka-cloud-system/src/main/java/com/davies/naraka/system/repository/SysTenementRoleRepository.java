package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysTenementRole;
import com.davies.naraka.system.domain.entity.SysTenementRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysTenementRoleRepository extends JpaRepository<SysTenementRole, SysTenementRoleId>, JpaSpecificationExecutor<SysTenementRole> {
}
