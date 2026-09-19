package com.digitalhub.repository;

import com.digitalhub.model.Download;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {
    List<Download> findByCustomerIdOrderByDownloadTimestampDesc(Long customerId);
    long countByProductId(Long productId);
}