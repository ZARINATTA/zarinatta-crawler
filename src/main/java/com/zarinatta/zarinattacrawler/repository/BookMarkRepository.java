package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    @Query("select b from BookMark b join fetch b.ticket join fetch b.user where b.isTimeOut = false")
    List<BookMark> findAllByTimeOutFalseJoinAll();
}
