package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = true AND is_blocked = false", nativeQuery = true)
    long count();
}