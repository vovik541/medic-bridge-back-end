package com.bridge.medic.support.repository;

import com.bridge.medic.support.model.ApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalLogRepository extends JpaRepository<ApprovalLog, Integer> {

}