package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysTenementRole;
import com.davies.naraka.system.domain.entity.SysTenementRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysTenementRoleRepository extends JpaRepository<SysTenementRole, SysTenementRoleId>, JpaSpecificationExecutor<SysTenementRole> {

    @Modifying
    @Query("delete from SysTenementRole u where u.id.tenementId = ?1")
    void deleteByTenementId(String tenementId);


    @Modifying
    @Query("delete from SysTenementRole u where u.id.roleCode = ?1")
    void deleteByRoleCode(String code);
}
