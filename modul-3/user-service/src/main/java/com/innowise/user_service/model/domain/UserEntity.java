package com.innowise.user_service.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String surname;

    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardInfoEntity> cards = new HashSet<>();

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    public UserEntity() {}

    public UserEntity(String name, String surname, String email, LocalDate birthDate){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
    }
}
