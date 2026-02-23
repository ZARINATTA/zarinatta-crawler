package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FailedTicketLogRepository extends JpaRepository<FailedTicketLog, Long> {

    @Query("SELECT f FROM FailedTicketLog f WHERE f.isSolved = false")
    List<FailedTicketLog> findAllUnsolvedFailer();

    Optional<FailedTicketLog> findByRequestUrlAndIsSolved(String requestUrl, boolean isSolved);

    @Transactional
    @Modifying
    @Query("UPDATE FailedTicketLog f SET f.isSolved = true WHERE f.id = :id")
    void updateIsSolvedById(@Param("id") Long id);
}
