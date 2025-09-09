package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    private final EntityManager em;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Ticket> tickets) {
        String sql = "INSERT INTO ticket (TICKET_TYPE, DEPART_DATE, DEPART_TIME, DEPART_STATION, ARRIVE_TIME, ARRIVE_STATION, PRICE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Ticket ticket = tickets.get(i);
                ps.setString(1, ticket.getTicketType());
                ps.setString(2, ticket.getDepartDate());
                ps.setString(3, ticket.getDepartTime());
                ps.setString(4, ticket.getDepartStation().name());
                ps.setString(5, ticket.getArriveTime());
                ps.setString(6, ticket.getArriveStation().name());
                ps.setString(7, ticket.getPrice());
            }
            @Override
            public int getBatchSize() {
                return tickets.size();
            }
        });
    }

    public Slice<Long> findTicketIdsAfterNow(String date, String time, Pageable pageable) {
        String jpql = "SELECT DISTINCT b.ticket.id FROM BookMark b WHERE (b.ticket.departDate > :date) OR (b.ticket.departDate = :date AND b.ticket.departTime > :time) ORDER BY b.ticket.id";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("date", date);
        query.setParameter("time", time);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize() + 1);
        List<Long> content = query.getResultList();
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
