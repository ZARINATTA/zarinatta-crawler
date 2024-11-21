package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.StationSection;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATION_ID")
    private Long id;

    @Column(name = "STATION_SECTION")
    @Enumerated(EnumType.STRING)
    private StationSection section;

    @Column(name = "STATION_NAME")
    private String name;

    @Column(name = "STATION_COUNT", columnDefinition = "BIGINT DEFAULT 0")
    private long count = 0;

    @Builder
    public Station(StationSection section, String name) {
        this.section = section;
        this.name = name;
        this.count = 0;
    }
}