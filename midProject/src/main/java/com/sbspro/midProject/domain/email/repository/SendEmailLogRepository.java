package com.sbspro.midProject.domain.email.repository;

import com.sbspro.midProject.domain.email.entity.SendEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendEmailLogRepository extends JpaRepository<SendEmailLog, Long> {
}