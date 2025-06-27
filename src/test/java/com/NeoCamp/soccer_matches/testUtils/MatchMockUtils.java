package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;

import java.time.LocalDateTime;

public class MatchMockUtils {

    public static MatchEntity flamengoVsCorinthiansAtMaracana(){
        ClubEntity flamengo = ClubMockUtils.flamengo();
        ClubEntity corinthians = ClubMockUtils.corinthians();
        StadiumEntity maracana = StadiumMockUtils.maracana();
        return new MatchEntity(corinthians, flamengo, 1, 2,
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

    public static MatchRequestDto custom(Long homeClubId, Long awayClubId, int homeGoals,
                                         int awayGoals, Long stadiumId){
        return new MatchRequestDto(homeClubId, awayClubId, homeGoals, awayGoals, stadiumId, LocalDateTime.now());
    }

    public static MatchResponseDto flamengoVsCorinthiansAtMaracanaResponseDto(){
        ClubResponseDto flamengo = ClubMockUtils.flamengoResponseDto();
        ClubResponseDto corinthians = ClubMockUtils.corinthiansResponseDto();
        StadiumResponseDto maracana = StadiumMockUtils.maracanaResponseDto();
        return new MatchResponseDto(1L, flamengo, corinthians, 1, 2, maracana,
                LocalDateTime.of(2005, 5, 12, 13, 30));
    }

    public static MatchResponseDto corinthiansVsGremioAtMorumbiResponseDto(){
        ClubResponseDto corinthians = ClubMockUtils.corinthiansResponseDto();
        ClubResponseDto gremio = ClubMockUtils.gremioResponseDto();
        StadiumResponseDto morumbi = StadiumMockUtils.morumbiResponseDto();
        return new MatchResponseDto(2L, corinthians, gremio, 1, 2, morumbi,
                LocalDateTime.of(2000, 12, 23, 14, 45));
    }

    public static MatchResponseDto custom(Long id, ClubResponseDto homeClub, ClubResponseDto awayClub,
                                          int homeGoals, int awayGoals, StadiumResponseDto stadiumId){
        return new MatchResponseDto(id, homeClub, awayClub, homeGoals, awayGoals, stadiumId,
                LocalDateTime.of(2002, 6, 18, 15, 30));
    }
}
