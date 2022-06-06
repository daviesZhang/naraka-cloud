package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysRoleAuthority;
import com.davies.naraka.system.domain.entity.SysRoleAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysRoleAuthorityRepository extends JpaRepository<SysRoleAuthority, SysRoleAuthorityId>, JpaSpecificationExecutor<SysRoleAuthority> {


    @Modifying
    @Query("delete from SysRoleAuthority u where u.id.authorityId = ?1")
    void deleteByAuthorityId(String authorityId);


    @Modifying
    @Query("delete from SysRoleAuthority u where u.id.roleCode = ?1")
    void deleteByRoleCode(String roleCode);
}
