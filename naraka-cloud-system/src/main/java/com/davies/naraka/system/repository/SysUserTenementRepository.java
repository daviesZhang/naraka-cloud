package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysUserTenement;
import com.davies.naraka.system.domain.entity.SysUserTenementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysUserTenementRepository extends JpaRepository<SysUserTenement, SysUserTenementId>, JpaSpecificationExecutor<SysUserTenement> {


    @Modifying
    @Query("delete from SysUserTenement u where u.id.userId = ?1")
    void deleteByUserId(String userId);
}
