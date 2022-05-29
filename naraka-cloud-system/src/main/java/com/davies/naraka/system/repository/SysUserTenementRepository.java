package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysUserTenement;
import com.davies.naraka.system.domain.entity.SysUserTenementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysUserTenementRepository extends JpaRepository<SysUserTenement, SysUserTenementId>, JpaSpecificationExecutor<SysUserTenement> {
}
