package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.testUtils.StateTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class ClubRepositoryTest {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private StateRepository stateRepository;


    private StateEntity sp;
    private Pageable pageable;

    @BeforeEach
    public void setup() {
        pageable = PageRequest.of(0, 10);

        StateEntity rs = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.RS);
        StateEntity rj = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.RJ);
        sp = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.SP);

        ClubEntity gremio = new ClubEntity("Grêmio", rs,
                LocalDate.of(1945, 7, 23), true);
        ClubEntity flamengo = new ClubEntity("Flamengo", rj,
                LocalDate.of(1970, 2, 10), true);
        ClubEntity corinthians = new ClubEntity("Corinthians", sp,
                LocalDate.of(1930, 4, 19), true);
        ClubEntity inativo = new ClubEntity("Inativo", sp,
                LocalDate.of(1950, 9, 27), false);

        clubRepository.saveAll(List.of(gremio, flamengo, corinthians, inativo));
    }

    @Test
    public void shouldFilterClubsByName() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters("l", null, null, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(1, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getName().toLowerCase().contains("l")));
    }

    @Test
    public void shouldFilterClubsByHomeState() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(null, sp, null, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(2, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getHomeState().equals(sp)));
    }
    @Test
    public void shouldFilterClubsByActive() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(null, null, false, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(1, clubs.getTotalElements());
    }

    @Test
    public void shouldFilterClubsByNameAndHomeState() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters("i", sp, null, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(2, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getName().toLowerCase().contains("i")));
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getHomeState().equals(sp)));
    }

    @Test
    public void shouldFilterClubsByNameAndActive() {
        Page<ClubEntity>  clubs = clubRepository.listClubsByFilters("g", null,
                true, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(2, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getName().toLowerCase().contains("g")));
    }

    @Test
    public void shouldFilterClubsByHomeStateAndActive() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(null, sp, false, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(1, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getHomeState().equals(sp)));
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getActive().equals(false)));
    }

    @Test
    public void shouldFilterClubsByNameAndHomeStateAndActive() {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters("in", sp, true, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(1, clubs.getTotalElements());
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getName().toLowerCase().contains("in")));
        Assertions.assertTrue(clubs.stream().allMatch(club -> club.getHomeState().equals(sp)));
    }

    @Test
    public void shouldReturnAllClubs_WhenFiltersAreNull(){
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(null, null, null, pageable);

        Assertions.assertFalse(clubs.isEmpty());
        Assertions.assertEquals(4, clubs.getTotalElements());
    }

    @Test
    public void shouldReturnEmptyPage_WhenPageIsOutOfBounds() {
        Pageable outOfBounds = PageRequest.of(15, 10);
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(null, null, null, outOfBounds);

        Assertions.assertTrue(clubs.isEmpty());
    }
}
