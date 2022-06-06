package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysUserRole;
import com.davies.naraka.system.domain.entity.SysUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole, SysUserRoleId>, JpaSpecificationExecutor<SysUserRole> {

    /**
     * 这个方法,如果userid关联多条,会多次sql删除
     *
     * @param userId
     */
//    void deleteByIdUserId(String userId);
    @Modifying
    @Query("delete from SysUserRole u where u.id.userId = ?1")
    void deleteByUserId(String userId);

    @Modifying
    @Query("delete from SysUserRole u where u.id.roleCode = ?1")
    void deleteByRoleCode(String roleCode);

}
