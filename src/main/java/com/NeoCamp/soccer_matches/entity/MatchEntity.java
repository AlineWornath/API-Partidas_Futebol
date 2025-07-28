package com.neocamp.soccer_matches.entity;

import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(unique = true, length = 36)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_club_id", nullable = false)
    private ClubEntity homeClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_club_id", nullable = false)
    private ClubEntity awayClub;

    private Integer homeGoals;
    private Integer awayGoals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private StadiumEntity stadium;

    private LocalDateTime matchDatetime;

    @Enumerated(EnumType.STRING)
    private MatchStatusEnum status;

    public MatchEntity(ClubEntity homeClub, ClubEntity awayClub, Integer homeGoals, Integer awayGoals,
                       StadiumEntity stadium, LocalDateTime matchDatetime) {
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.stadium = stadium;
        this.matchDatetime = matchDatetime;
    }
}
