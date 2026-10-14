package com.example.portfolio.repository;

import com.example.portfolio.entity.VisitorClassification;
import com.example.portfolio.entity.VisitorClassificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitorClassificationRepository extends JpaRepository<VisitorClassificationRecord, Long> {

    Optional<VisitorClassificationRecord> findByVisitDateAndVisitorKeyHash(LocalDate visitDate, String visitorKeyHash);

    long countByVisitDateAndClassification(LocalDate visitDate, VisitorClassification classification);

    @Query("select count(distinct v.visitorKeyHash) from VisitorClassificationRecord v " +
            "where v.visitDate between :start and :end and v.classification = :classification")
    long countUniqueVisitors(@Param("start") LocalDate start,
                             @Param("end") LocalDate end,
                             @Param("classification") VisitorClassification classification);

    @Query("select min(v.visitDate) from VisitorClassificationRecord v")
    LocalDate findFirstTrackedDate();
}
