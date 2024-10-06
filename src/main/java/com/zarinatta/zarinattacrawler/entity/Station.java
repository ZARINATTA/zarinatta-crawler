package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.StationSection;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "STATION")
@Table(name = "STATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;

    @Column(name = "station_section")
    @Enumerated(EnumType.STRING)
    private StationSection section;

    @Column(name = "station_name")
    private String name;

    @Column(name = "station_count")
    @Builder.Default
    private long count = 0;
}