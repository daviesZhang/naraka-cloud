package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysRoleAuthority;
import com.davies.naraka.system.domain.entity.SysRoleAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysRoleAuthorityRepository extends JpaRepository<SysRoleAuthority, SysRoleAuthorityId>, JpaSpecificationExecutor<SysRoleAuthority> {
}
