package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {

    @Query("SELECT DISTINCT t FROM Ticket t JOIN FETCH t.bookMarks b JOIN FETCH b.user u WHERE t.id IN :ticketIds ORDER BY t.id")
    List<Ticket> findTicketsWithDetailsByIds(@Param("ticketIds") List<Long> ticketIds);
}
