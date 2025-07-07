package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.mapper.StadiumMapper;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class StadiumServiceTest {

    private final StadiumRepository stadiumRepository = Mockito.mock(StadiumRepository.class);
    private final StadiumMapper stadiumMapper = Mappers.getMapper(StadiumMapper.class);
    private final CepService cepService = Mockito.mock(CepService.class);
    private StadiumService stadiumService;
    private final Pageable pageable = PageRequest.of(0, 10);
    private StadiumEntity maracanaEntity, morumbiEntity;
    private StadiumRequestDto maracanaRequestDto, morumbiRequestDto;


    @BeforeEach
    public void setUp() {
        stadiumService = new StadiumService(stadiumRepository, stadiumMapper, cepService);
        maracanaEntity = StadiumMockUtils.maracana();
        morumbiEntity = StadiumMockUtils.morumbi();

        maracanaRequestDto = StadiumMockUtils.maracanaRequestDto();
        morumbiRequestDto = StadiumMockUtils.morumbiRequestDto();
    }
    @Test
    public void shouldListAllStadiums() {
        Page<StadiumEntity> stadiums = new PageImpl<>(List.of(maracanaEntity, morumbiEntity), pageable, 2);

        Mockito.when(stadiumRepository.findAll(pageable)).thenReturn(stadiums);

        Page<StadiumResponseDto> result = stadiumService.findAll(pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("Maracanã", result.getContent().get(0).getName());
        Assertions.assertEquals("Morumbi", result.getContent().get(1).getName());
    }

    @Test
    public void shouldReturnStadiumByIdSuccessfully() {
        Long id = 1L;
        maracanaEntity.setId(id);

        Mockito.when(stadiumRepository.findById(id)).thenReturn(Optional.of(maracanaEntity));

        StadiumResponseDto result = stadiumService.findById(id);

        Assertions.assertEquals("Maracanã", result.getName());
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    public void shouldThrowException_whenFindByIdWithInvalidId() {
        Long invalidId = -2L;

        Mockito.when(stadiumRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> stadiumService.findById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Stadium not found: "));
    }

    @Test
    public void shouldReturnStadiumEntityByIdSuccessfully() {
        Long id = 2L;
        morumbiEntity.setId(id);

        Mockito.when(stadiumRepository.findById(id)).thenReturn(Optional.of(morumbiEntity));

        StadiumEntity result = stadiumService.findEntityById(2L);

        Assertions.assertEquals("Morumbi", result.getName());
        Assertions.assertEquals(2L, result.getId());
    }

    @Test
    public void shouldThrowException_whenFindEntityByIdWithInvalidId() {
        Long invalidId = -1L;

        Mockito.when(stadiumRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> stadiumService.findEntityById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Stadium not found: "));
    }

    @Test
    public void shouldSaveStadiumSuccessfully() {
        Mockito.when(stadiumRepository.save(Mockito.any())).thenAnswer(invocation -> {
                   StadiumEntity entity = invocation.getArgument(0);
                   entity.setId(2L);
                   return entity;
                });
        StadiumResponseDto result = stadiumService.save(maracanaRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Maracanã", result.getName());
        Assertions.assertEquals(2L, result.getId());
    }

    @Test
    public void shouldUpdateStadiumSuccessfully() {
        Long existingStadiumId = 8L;
        StadiumEntity existingStadium = StadiumMockUtils.custom("Old Name");
        existingStadium.setId(existingStadiumId);

        StadiumRequestDto updateRequest = StadiumMockUtils.customRequest("New Name");

        StadiumResponseDto updatedResponse = StadiumMockUtils.customResponse("New Name");
        updatedResponse.setId(existingStadiumId);

        Mockito.when(stadiumRepository.findById(existingStadiumId)).thenReturn(Optional.of(existingStadium));
        Mockito.when(stadiumRepository.save(existingStadium)).thenReturn(existingStadium);

        StadiumResponseDto result = stadiumService.update(existingStadiumId, updateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(8L, result.getId());
        Assertions.assertEquals("New Name", result.getName());
        Assertions.assertEquals("New Name", existingStadium.getName());
    }

    @Test
    public void shouldThrowException_whenUpdateStadiumWithInvalidId() {
        Long invalidId = -1L;

        Mockito.when(stadiumRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> stadiumService.update(invalidId, morumbiRequestDto));

        Assertions.assertTrue(exception.getMessage().contains("Stadium not found: "));
    }
}
