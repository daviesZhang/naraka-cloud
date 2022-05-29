package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysUserRole;
import com.davies.naraka.system.domain.entity.SysUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole, SysUserRoleId>, JpaSpecificationExecutor<SysUserRole> {
}
