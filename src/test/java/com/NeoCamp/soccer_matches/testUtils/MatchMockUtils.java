package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;

import java.time.LocalDateTime;

public class MatchMockUtils {

    public static final Long FLAMENGO_ID = 1L;
    public static final String FLAMENGO_NAME = "Flamengo";
    public static final Long CORINTHIANS_ID = 2L;
    public static final String CORINTHIANS_NAME = "Corinthians";
    public static final Long GREMIO_ID = 3L;
    public static final String GREMIO_NAME = "Grêmio";
    public static final Long MARACANA_ID = 5L;
    public static final String MARACANA_NAME = "Maracanã";
    public static final Long MORUMBI_ID = 6L;
    public static final String MORUMBI_NAME = "Morumbi";


    public static MatchEntity flamengoVsCorinthiansAtMaracana(){
        ClubEntity flamengo = ClubMockUtils.flamengo();
        ClubEntity corinthians = ClubMockUtils.corinthians();
        StadiumEntity maracana = StadiumMockUtils.maracana();
        return new MatchEntity(flamengo, corinthians, 1, 2,
                maracana, LocalDateTime.of(2020, 1, 1, 12, 40));
    }

    public static MatchEntity corinthiansVsGremioAtMorumbi(){
        ClubEntity corinthians = ClubMockUtils.corinthians();
        ClubEntity gremio = ClubMockUtils.gremio();
        StadiumEntity morumbi = StadiumMockUtils.morumbi();
        return new MatchEntity(corinthians, gremio, 1, 2,
                morumbi, LocalDateTime.of(2023, 4, 25, 15, 45));
    }

    public static MatchEntity custom(ClubEntity homeClub, ClubEntity awayClub, int homeGoals,
                                            int awayGoals, StadiumEntity stadium){
        return new MatchEntity(homeClub, awayClub, homeGoals, awayGoals, stadium, LocalDateTime.now());
    }

    public static MatchRequestDto flamengoVsCorinthiansAtMaracanaRequestDto(){
        return new MatchRequestDto(1L, 2L, 1, 2, 3L,
                LocalDateTime.of(2015, 4, 25, 16, 20));
    }

    public static MatchRequestDto corinthiansVsGremioAtMorumbiRequestDto(){
        return new MatchRequestDto(4L, 5L, 3, 4, 6L,
                LocalDateTime.of(2010, 7, 10, 17, 45));
    }

    public static MatchRequestDto customRequest(Long homeClubId, Long awayClubId, int homeGoals,
                                                int awayGoals, Long stadiumId){
        return new MatchRequestDto(homeClubId, awayClubId, homeGoals, awayGoals, stadiumId, LocalDateTime.now());
    }

    public static MatchResponseDto flamengoVsCorinthiansAtMaracanaResponseDto(){
        return new MatchResponseDto(1L, FLAMENGO_ID, FLAMENGO_NAME,
                CORINTHIANS_ID, CORINTHIANS_NAME,
                1, 2, MARACANA_ID, MARACANA_NAME ,
                LocalDateTime.of(2005, 5, 12, 13, 30));
    }

    public static MatchResponseDto corinthiansVsGremioAtMorumbiResponseDto(){
        return new MatchResponseDto(2L, CORINTHIANS_ID, CORINTHIANS_NAME,
                GREMIO_ID, GREMIO_NAME,
                2, 0, MORUMBI_ID, MORUMBI_NAME,
                LocalDateTime.of(2000, 12, 23, 14, 45));
    }

    public static MatchResponseDto customResponse(Long id, Long homeClubId, String homeClubName, Long awayClubId,
                                                  String awayClubName, int homeGoals, int awayGoals, Long stadiumId,
                                                  String stadiumName){
        return new MatchResponseDto(id, homeClubId, homeClubName, awayClubId, awayClubName, homeGoals, awayGoals,
                stadiumId, stadiumName, LocalDateTime.of(2002, 6, 18, 15, 30));
    }
}
