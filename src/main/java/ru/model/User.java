package ru.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name="user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstName;

    String lastName;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    List<Card> cards;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;
}
