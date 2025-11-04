package com.innowise.user_service.repository;

import com.innowise.user_service.model.domain.CardInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfoEntity, Long> {

    Page<CardInfoEntity> findAll(Pageable pageable);

    List<CardInfoEntity> findByUserId(Long userId);

    @Query("SELECT c FROM CardInfoEntity c WHERE c.holder LIKE %:holderName%")
    List<CardInfoEntity> findByHolderContainingJPQL(@Param("holderName") String holderName);

    @Query(value = "SELECT * FROM card_info c WHERE c.number = :cardNumber", nativeQuery = true)
    Optional<CardInfoEntity> findByCardNumberNative(@Param("cardNumber") Long cardNumber);
}