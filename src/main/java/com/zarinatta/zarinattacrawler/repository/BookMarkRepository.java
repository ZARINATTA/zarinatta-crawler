package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    @Query("select b from BookMark b join fetch b.ticket join fetch b.user where b.ticket.departTime > :now")
    List<BookMark> findAllByAfterNowJoinAll(@Param("now") String now);

    @Query("select b from BookMark b join fetch b.ticket join fetch b.user " +
            "where ((b.ticket.departDate = :date and b.ticket.departTime > :time) or b.ticket.departDate > :date) and b.isSent = false")
    List<BookMark> findAllByAfterNow(@Param("date") String date, @Param("time") String time);

    @Query("select b from BookMark b join fetch b.ticket join fetch b.user " +
            "where ((b.ticket.departDate = :date and b.ticket.departTime > :time) or b.ticket.departDate > :date) and b.isSent = false")
    Page<BookMark> findChunkByAfterNow(@Param("date") String date, @Param("time") String time, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BookMark b WHERE b.ticket.id IN :ids")
    void deleteByTicketIdIn(@Param("ids") List<Long> ids);}
