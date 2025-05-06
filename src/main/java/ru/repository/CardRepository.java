package ru.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.model.Card;
import ru.model.CardStatus;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c WHERE (:status IS NULL OR c.status = :status) AND (:userId IS NULL OR c.owner.id = :userId)")
    Page<Card> findByStatusAndUserId(@Param("status") String status,
                                     @Param("userId") Long userId,
                                     Pageable pageable);
    Page<Card> findByOwnerId(Object unknownAttr1, Pageable pageable);

    @Query("""
    SELECT c FROM Card c
    WHERE (:status IS NULL OR c.status = :status)
      AND (:userId IS NULL OR c.owner.id = :userId)
      AND (:dateFrom IS NULL OR c.expirationDate >= :dateFrom)
      AND (:dateTo IS NULL OR c.expirationDate <= :dateTo)
    """)
    Page<Card> searchCards(@Param("userId") Long userId,
                           @Param("status") CardStatus status,
                           @Param("dateFrom") LocalDate dateFrom,
                           @Param("dateTo") LocalDate dateTo,
                           Pageable pageable);
}