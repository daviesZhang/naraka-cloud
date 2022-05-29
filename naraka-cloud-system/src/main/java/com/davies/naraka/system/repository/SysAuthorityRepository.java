package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysAuthorityRepository extends JpaRepository<SysAuthority, String>, JpaSpecificationExecutor<SysAuthority> {
}
