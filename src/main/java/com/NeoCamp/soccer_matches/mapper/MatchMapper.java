package com.neocamp.soccer_matches.mapper;

import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper (componentModel = "spring", uses = {ClubMapper.class, StadiumMapper.class})
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    MatchEntity toEntity(MatchRequestDto dto, ClubEntity homeClub, ClubEntity awayClub, StadiumEntity stadium);

    @Mapping(source = "homeClub.id", target = "homeClubId")
    @Mapping(source = "homeClub.name", target = "homeClubName")
    @Mapping(source = "awayClub.id", target = "awayClubId")
    @Mapping(source = "awayClub.name", target = "awayClubName")
    @Mapping(source = "stadium.id", target = "stadiumId")
    @Mapping(source = "stadium.name", target = "stadiumName")
    MatchResponseDto toDto(MatchEntity match);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "homeClub", ignore = true)
    @Mapping(target = "awayClub", ignore = true)
    @Mapping(target = "stadium", ignore = true)
    void updateEntityFromDto(MatchRequestDto dto, @MappingTarget MatchEntity entity);
}
