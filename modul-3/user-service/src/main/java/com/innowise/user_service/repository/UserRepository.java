package com.innowise.user_service.repository;

import com.innowise.user_service.model.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.cards WHERE u.id = :id")
    Optional<UserEntity> findByIdWithCards(@Param("id") Long id);

    @Query("SELECT u FROM UserEntity u WHERE u.surname = :surname")
    List<UserEntity> findBySurnameJPQL(@Param("surname") String surname);

    @Query(value = "SELECT * FROM users u WHERE u.name = :name", nativeQuery = true)
    List<UserEntity> findByNameNative(@Param("name") String name);

    Page<UserEntity> findAll(Pageable pageable);
}