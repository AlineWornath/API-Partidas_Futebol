package com.neocamp.soccer_matches.entity;

import com.neocamp.soccer_matches.valueobject.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stadium")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private Address address;

    public StadiumEntity(String name) {
        this.name = name;
    }
}
