package com.bridge.medic.support.repository;

import com.bridge.medic.support.ApprovalStatus;
import com.bridge.medic.support.model.ApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalLogRepository extends JpaRepository<ApprovalLog, Integer> {
    List<ApprovalLog> findAllByStatus(ApprovalStatus status);
}