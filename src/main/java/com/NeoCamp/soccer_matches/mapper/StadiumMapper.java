package com.neocamp.soccer_matches.mapper;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StadiumMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", ignore = true)
    StadiumEntity toEntity(StadiumRequestDto dto);

    StadiumResponseDto toDto(StadiumEntity stadium);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", ignore = true)
    void updateEntityFromDto(StadiumRequestDto dto, @MappingTarget StadiumEntity entity);
}
