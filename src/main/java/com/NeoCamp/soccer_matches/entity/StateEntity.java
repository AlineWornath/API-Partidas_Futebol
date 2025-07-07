package com.neocamp.soccer_matches.entity;

import com.neocamp.soccer_matches.enums.StateCodeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "state")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private StateCodeEnum code;

    public StateEntity(String name, StateCodeEnum code) {
        this.name = name;
        this.code = code;
    }
}
