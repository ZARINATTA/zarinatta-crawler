package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TicketRepositoryCustom {
    void saveAll(List<Ticket> tickets);
    Slice<Long> findTicketIdsAfterNow(String date, String time, Pageable pageable);
}
