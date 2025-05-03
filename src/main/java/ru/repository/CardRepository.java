package ru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}