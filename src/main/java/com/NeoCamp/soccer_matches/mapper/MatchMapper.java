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

    MatchResponseDto toDto(MatchEntity match);

    void updateEntityFromDto(MatchRequestDto dto, @MappingTarget MatchEntity entity);
}
