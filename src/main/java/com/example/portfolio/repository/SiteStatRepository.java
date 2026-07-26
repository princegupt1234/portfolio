package com.example.portfolio.repository;

import com.example.portfolio.entity.SiteStat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SiteStatRepository extends JpaRepository<SiteStat, Long> {
    Optional<SiteStat> findByStatDate(LocalDate date);
    List<SiteStat> findTop30ByOrderByStatDateDesc();
}
