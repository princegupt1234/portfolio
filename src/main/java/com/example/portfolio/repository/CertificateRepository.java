package com.example.portfolio.repository;

import com.example.portfolio.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByOrderBySortOrderAsc();
}
