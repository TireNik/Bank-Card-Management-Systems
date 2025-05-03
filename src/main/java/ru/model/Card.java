package ru.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String cardNumber;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @Column(nullable = false)
    LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CardStatus status;

    @Column(nullable = false)
    Double balance;
}
