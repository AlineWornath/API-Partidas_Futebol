package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class MatchRepositoryTest {

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    private StateRepository stateRepository;

    private StateEntity rs;
    private StateEntity rj;
    private StateEntity sp;
    private ClubEntity gremio;
    private Long gremioId;
    private StadiumEntity maracana;
    private Pageable pageable;
    @Autowired
    private StadiumRepository stadiumRepository;

    @BeforeEach
    public void setup() {
        pageable = PageRequest.of(0, 10);

        rs = stateRepository.findByCode(StateCode.RS).orElseThrow(() -> new RuntimeException("State not found"));
        rj = stateRepository.findByCode(StateCode.RJ).orElseThrow(() -> new RuntimeException("State not found"));
        sp = stateRepository.findByCode(StateCode.SP).orElseThrow(() -> new RuntimeException("State not found"));

        gremio = new ClubEntity("Grêmio", rs,
                LocalDate.of(1945, 7, 23), true);
        ClubEntity flamengo = new ClubEntity("Flamengo", rj,
                LocalDate.of(1970, 2, 10), true);
        ClubEntity corinthians = new ClubEntity("Corinthians", sp,
                LocalDate.of(1930, 4, 19), true);
        ClubEntity inativo = new ClubEntity("Inativo", sp,
                LocalDate.of(1950, 9, 27), false);

        clubRepository.saveAll(List.of(gremio, flamengo, corinthians, inativo));

        gremioId = gremio.getId();

        maracana = stadiumRepository.save(new StadiumEntity("Maracanã"));
        StadiumEntity morumbi = stadiumRepository.save(new StadiumEntity("Morumbi"));

        MatchEntity gremioVsFlamengoAtMaracana = new MatchEntity(gremio, flamengo, 2, 1,
                maracana, LocalDateTime.of(2015, 10, 5, 12, 45));

        MatchEntity corinthiansVsGremioAtMorumbi = new MatchEntity(corinthians, gremio, 1, 1,
                morumbi, LocalDateTime.of(2020, 1, 25, 15, 50));

        matchRepository.saveAll(List.of(gremioVsFlamengoAtMaracana, corinthiansVsGremioAtMorumbi));
    }

    @Test
    public void shouldFilterMatchesByClub(){
        Page<MatchEntity> matches = matchRepository.listMatchesByFilters(gremio, null, pageable);

        Assertions.assertNotNull(matches);
        Assertions.assertEquals(2, matches.getTotalElements());
        Assertions.assertTrue(matches.stream().allMatch(
                match -> gremio.equals(match.getHomeClub()) || gremio.equals(match.getAwayClub())));
    }

    @Test
    public void shouldFilterMatchesByStadium(){
        Page<MatchEntity> matches = matchRepository.listMatchesByFilters(null, maracana, pageable);

        Assertions.assertNotNull(matches);
        Assertions.assertEquals(1, matches.getTotalElements());
        Assertions.assertTrue(matches.stream().allMatch(match -> maracana.equals(match.getStadium())));
    }

    @Test
    public void shouldFilterMatchesByClubAndStadium(){
        Page<MatchEntity> matches = matchRepository.listMatchesByFilters(gremio, maracana, pageable);

        Assertions.assertNotNull(matches);
        Assertions.assertEquals(1, matches.getTotalElements());
        Assertions.assertTrue(matches.stream().allMatch(match -> gremio.equals(match.getHomeClub())));
        Assertions.assertTrue(matches.stream().allMatch(match -> maracana.equals(match.getStadium())));
    }

    @Test
    public void shouldCalculateClubStats() {
        ClubStatsResponseDto clubStats = matchRepository.getClubStats(gremioId);

        Assertions.assertEquals("Grêmio", clubStats.getClubName());
        Assertions.assertEquals(1, clubStats.getTotalWins());
        Assertions.assertEquals(1, clubStats.getTotalDraws());
        Assertions.assertEquals(0, clubStats.getTotalLosses());
        Assertions.assertEquals(3, clubStats.getGoalsScored());
    }

    @Test
    public void shouldCalculateClubVersusOpponentsStats() {
        List<ClubVersusClubStatsDto> statsList = matchRepository.getClubVersusOpponentsStats(gremioId);

        Assertions.assertEquals(2, statsList.size());
        assertThat(statsList).extracting(ClubVersusClubStatsDto::getOpponentName).contains("Corinthians", "Flamengo");
    }
}
