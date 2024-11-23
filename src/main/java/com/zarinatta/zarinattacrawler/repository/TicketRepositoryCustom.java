package com.zarinatta.zarinattacrawler.repository;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryCustom {

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
}
