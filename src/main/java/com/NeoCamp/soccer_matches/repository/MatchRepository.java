package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.dto.club.ClubRankingDto;
import com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.entity.MatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
    @Query("""
        SELECT m FROM MatchEntity m
        WHERE (:clubId IS NULL OR m.homeClub.id = :clubId OR m.awayClub.id = :clubId)
        AND (:stadiumId IS NULL OR m.stadium.id = :stadiumId)
        AND (:isRout IS NULL OR (ABS(m.homeGoals - m.awayGoals) >= 3))
        AND (:isHome IS NULL OR (m.homeClub.id = :clubId AND :isHome = TRUE))
        AND (:isAway IS NULL OR (m.awayClub.id = :clubId AND :isAway = TRUE))
        """)
    Page<MatchEntity> listMatchesByFilters(
            @Param("clubId") Long clubId,
            @Param("stadiumId")Long stadiumId,
            @Param("isRout") Boolean  isRout,
            @Param("isHome") Boolean isHome,
            @Param("isAway") Boolean isAway,
            Pageable pageable
            );

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto(
            :id,
            (SELECT c.name FROM ClubEntity c WHERE c.id = :id),
            COUNT(CASE WHEN (m.homeClub.id = :id AND m.homeGoals > m.awayGoals)
                   OR (m.awayClub.id = :id AND m.awayGoals > m.homeGoals) THEN 1 END),
            COUNT(CASE WHEN m.homeGoals = m.awayGoals THEN 1 END),
            COUNT(CASE WHEN (m.homeClub.id = :id AND m.homeGoals < m.awayGoals)
                   OR (m.awayClub.id = :id AND m.awayGoals < m.homeGoals) THEN 1 END),
            SUM(CASE WHEN m.homeClub.id = :id THEN m.homeGoals
                     WHEN m.awayClub.id = :id THEN m.awayGoals ELSE 0 END),
            SUM(CASE WHEN m.homeClub.id = :id THEN m.awayGoals
                     WHEN m.awayClub.id = :id THEN m.homeGoals ELSE 0 END)
        )
        FROM MatchEntity m
        WHERE (m.homeClub.id = :id OR m.awayClub.id = :id)
        AND (:isHome IS NULL OR (m.homeClub.id = :id AND :isHome = TRUE))
        AND (:isAway IS NULL OR (m.awayClub.id = :id AND :isAway = TRUE))
    """)
    ClubStatsResponseDto getClubStats(
            @Param("id") Long id,
            @Param("isHome") Boolean isHome,
            @Param("isAway") Boolean isAway);

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto(
            :id,
            (SELECT c.name FROM ClubEntity c WHERE c.id = :id),
            CASE WHEN m.homeClub.id = :id THEN m.awayClub.id ELSE m.homeClub.id END,
            CASE WHEN m.homeClub.id = :id THEN m.awayClub.name ELSE m.homeClub.name END,
            COUNT(CASE WHEN (m.homeClub.id = :id AND m.homeGoals > m.awayGoals)
                OR (m.awayClub.id = :id AND m.awayGoals > m.homeGoals) THEN 1 END),
            COUNT(CASE WHEN m.homeGoals = m.awayGoals THEN 1 END),
            COUNT(CASE WHEN (m.homeClub.id = :id AND m.homeGoals < m.awayGoals)
                OR (m.awayClub.id = :id AND m.awayGoals < m.homeGoals) THEN 1 END),
            SUM(CASE WHEN m.homeClub.id = :id THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN m.homeClub.id = :id THEN m.awayGoals ELSE m.homeGoals END)
       )
       FROM MatchEntity m
       WHERE (m.homeClub.id = :id OR m.awayClub.id = :id)
       AND (:isHome IS NULL OR (m.homeClub.id = :id AND :isHome = TRUE))
       AND (:isAway IS NULL OR (m.awayClub.id = :id AND :isAway = TRUE))
       GROUP BY
            CASE WHEN m.homeClub.id = :id THEN m.awayClub.id ELSE m.homeClub.id END,
            CASE WHEN m.homeClub.id = :id THEN m.awayClub.name ELSE m.homeClub.name END
""")
    List<ClubVersusClubStatsDto> getClubVersusOpponentsStats(
            @Param("id") Long id,
            @Param("isHome") Boolean isHome,
            @Param("isAway") Boolean isAway);

    @Query("""
       SELECT new com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto(
            :clubId,
            (SELECT c.name FROM ClubEntity c WHERE c.id = :clubId),
            CASE WHEN m.homeClub.id = :clubId THEN m.awayClub.id ELSE m.homeClub.id END,
            CASE WHEN m.homeClub.id = :clubId THEN m.awayClub.name ELSE m.homeClub.name END,
            COUNT(CASE WHEN (m.homeClub.id = :clubId AND m.homeGoals > m.awayGoals)
                OR (m.awayClub.id = :clubId AND m.awayGoals > m.homeGoals) THEN 1 END),
            COUNT(CASE WHEN m.homeGoals = m.awayGoals THEN 1 END),
            COUNT(CASE WHEN (m.homeClub.id = :clubId AND m.homeGoals < m.awayGoals)
                OR (m.awayClub.id = :clubId AND m.awayGoals < m.homeGoals) THEN 1 END),
            SUM(CASE WHEN m.homeClub.id = :clubId THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN m.homeClub.id = :clubId THEN m.awayGoals ELSE m.homeGoals END)
       )
       FROM MatchEntity m
       WHERE ((m.homeClub.id = :clubId AND m.awayClub.id = :opponentId)
          OR (m.awayClub.id = :clubId AND m.homeClub.id = :opponentId))
       AND (:rout IS NULL OR (ABS(m.homeGoals - m.awayGoals) >= 3))
       AND (:filterAsHome IS NULL OR (m.homeClub.id = :clubId AND :filterAsHome = TRUE))
       AND (:filterAsAway IS NULL OR (m.awayClub.id = :clubId AND :filterAsAway = TRUE))
""")
    ClubVersusClubStatsDto getHeadToHeadStats(
            @Param("clubId") Long clubId,
            @Param("opponentId") Long opponentId,
            @Param("rout") Boolean rout,
            @Param("filterAsHome") Boolean filterAsHome,
            @Param("filterAsAway") Boolean filterAsAway);

    @Query("""
       SELECT m FROM MatchEntity m
       WHERE ((m.homeClub.id = :clubId AND m.awayClub.id = :opponentId)
          OR (m.awayClub.id = :clubId AND m.homeClub.id = :opponentId))
       AND (:rout IS NULL OR (ABS(m.homeGoals - m.awayGoals) >= 3))
       AND (:isHome IS NULL OR (m.homeClub.id = :clubId AND :isHome = TRUE))
       AND (:isAway IS NULL OR (m.awayClub.id = :clubId AND :isAway = TRUE))
""")
    List<MatchEntity> getHeadToHeadMatches(
            @Param("clubId") Long clubId,
            @Param("opponentId")  Long opponentId,
            @Param("isRout") Boolean isRout,
            @Param("isHome") Boolean isHome,
            @Param("isAway") Boolean isAway);

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubRankingDto(
            c.id,
            c.name,
            COUNT(m.id),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id)
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals < m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals < m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                ELSE 0 END)
        )
        FROM ClubEntity c
        JOIN MatchEntity m ON c.id = m.homeClub.id OR c.id = m.awayClub.id
        GROUP BY c.id, c.name
        ORDER BY COUNT (m.id) DESC
""")
    List<ClubRankingDto> getClubRankingByTotalMatches();

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubRankingDto(
            c.id,
            c.name,
            COUNT(m.id),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id)
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals < m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals < m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                ELSE 0 END)
        )
        FROM ClubEntity c
        JOIN MatchEntity m ON c.id = m.homeClub.id OR c.id = m.awayClub.id
        GROUP BY c.id, c.name
        HAVING SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END) > 0
        ORDER BY SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END) DESC
""")
    List<ClubRankingDto> getClubRankingByTotalWins();

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubRankingDto(
            c.id,
            c.name,
            COUNT(m.id),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id)
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals < m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals < m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                ELSE 0 END)
        )
        FROM ClubEntity c
        JOIN MatchEntity m ON c.id = m.homeClub.id OR c.id = m.awayClub.id
        GROUP BY c.id, c.name
        HAVING SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END) > 0
        ORDER BY SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END) DESC
""")
    List<ClubRankingDto> getClubRankingByTotalGoals();

    @Query("""
        SELECT new com.neocamp.soccer_matches.dto.club.ClubRankingDto(
            c.id,
            c.name,
            COUNT(m.id),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id)
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals < m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals < m.homeGoals) THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.homeClub.id = c.id THEN m.homeGoals ELSE m.awayGoals END),
            SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                ELSE 0 END)
        )
        FROM ClubEntity c
        JOIN MatchEntity m ON c.id = m.homeClub.id OR c.id = m.awayClub.id
        GROUP BY c.id, c.name
        HAVING SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                    OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                   WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                   ELSE 0 END) > 0
        ORDER BY SUM(CASE WHEN (m.homeClub.id = c.id AND m.homeGoals > m.awayGoals)
                      OR (m.awayClub.id = c.id AND m.awayGoals > m.homeGoals) THEN 3
                      WHEN m.homeGoals = m.awayGoals AND (m.homeClub.id = c.id OR m.awayClub.id = c.id) THEN 1
                      ELSE 0 END) DESC
""")
    List<ClubRankingDto> getClubRankingByTotalPoints();
}
