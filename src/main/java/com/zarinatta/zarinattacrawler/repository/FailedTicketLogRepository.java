package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface FailedTicketLogRepository extends JpaRepository<FailedTicketLog, Long> {
}
