package com.example.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "visitor_classifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"visitDate", "visitorKeyHash"}))
public class VisitorClassificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false, length = 64)
    private String visitorKeyHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private VisitorClassification classification;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    public String getVisitorKeyHash() { return visitorKeyHash; }
    public void setVisitorKeyHash(String visitorKeyHash) { this.visitorKeyHash = visitorKeyHash; }
    public VisitorClassification getClassification() { return classification; }
    public void setClassification(VisitorClassification classification) { this.classification = classification; }
}
