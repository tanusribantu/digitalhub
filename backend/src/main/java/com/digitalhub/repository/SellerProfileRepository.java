package com.digitalhub.repository;

import com.digitalhub.model.SellerProfile;
import com.digitalhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUserId(Long userId);
    Optional<SellerProfile> findByUser(User user);
}