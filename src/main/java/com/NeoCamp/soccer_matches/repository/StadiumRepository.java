package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.entity.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumEntity, Long> {
    Optional<StadiumEntity> findByUuid(UUID uuid);
    
    boolean existsByUuid(UUID uuid);
}
