package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {
    @Query("SELECT c FROM ClubEntity c " +
            "WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:homeState IS NULL OR c.homeState = :homeState) " +
            "AND (:active IS NULL OR c.active = :active)")
    Page<ClubEntity> listClubsByFilters(
            @Param("name") String name,
            @Param("homeState") StateEntity homeState,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
