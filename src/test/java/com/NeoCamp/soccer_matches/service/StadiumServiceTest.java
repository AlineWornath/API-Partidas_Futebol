package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.mapper.StadiumMapper;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @Mock
    private StadiumMapper stadiumMapper;

    @InjectMocks
    private StadiumService stadiumService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private StadiumEntity maracanaEntity, morumbiEntity;
    private StadiumRequestDto maracanaRequestDto, morumbiRequestDto;
    private StadiumResponseDto maracanaResponseDto;


    @BeforeEach
    public void setUp() {
        maracanaEntity = StadiumMockUtils.maracana();
        morumbiEntity = StadiumMockUtils.morumbi();

        maracanaRequestDto = StadiumMockUtils.maracanaRequestDto();
        morumbiRequestDto = StadiumMockUtils.morumbiRequestDto();

        maracanaResponseDto = StadiumMockUtils.maracanaResponseDto();
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
        Mockito.when(stadiumMapper.toDto(maracanaEntity)).thenReturn(maracanaResponseDto);

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
        maracanaEntity.setId(2L);
        maracanaResponseDto.setId(2L);

        Mockito.when(stadiumMapper.toEntity(maracanaRequestDto)).thenReturn(maracanaEntity);
        Mockito.when(stadiumRepository.save(maracanaEntity)).thenReturn(maracanaEntity);
        Mockito.when(stadiumMapper.toDto(maracanaEntity)).thenReturn(maracanaResponseDto);

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

        StadiumRequestDto updateRequest = StadiumMockUtils.customRequestDto("New Name");

        StadiumResponseDto updatedResponse = StadiumMockUtils.customResponseDto("New Name");
        updatedResponse.setId(existingStadiumId);

        Mockito.when(stadiumRepository.findById(existingStadiumId)).thenReturn(Optional.of(existingStadium));
        Mockito.when(stadiumRepository.save(existingStadium)).thenReturn(existingStadium);
        Mockito.when(stadiumMapper.toDto(existingStadium)).thenReturn(updatedResponse);

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
