package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.entity.CaseStepHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseStepHistoryRepository extends JpaRepository<CaseStepHistory, Long> {
}