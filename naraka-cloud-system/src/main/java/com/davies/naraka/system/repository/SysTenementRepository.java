package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysTenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysTenementRepository extends JpaRepository<SysTenement, String>, JpaSpecificationExecutor<SysTenement> {
}
