package com.example.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

/**
 * One row per calendar day. Counters are incremented as visitors/actions occur,
 * powering the admin analytics dashboard without needing an external analytics service.
 */
@Entity
@Table(name = "site_stats", uniqueConstraints = @UniqueConstraint(columnNames = "statDate"))
public class SiteStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate statDate;

    private Long portfolioViews = 0L;
    private Long resumeDownloads = 0L;
    private Long messagesReceived = 0L;
    private Long projectClicks = 0L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public Long getPortfolioViews() { return portfolioViews; }
    public void setPortfolioViews(Long portfolioViews) { this.portfolioViews = portfolioViews; }
    public Long getResumeDownloads() { return resumeDownloads; }
    public void setResumeDownloads(Long resumeDownloads) { this.resumeDownloads = resumeDownloads; }
    public Long getMessagesReceived() { return messagesReceived; }
    public void setMessagesReceived(Long messagesReceived) { this.messagesReceived = messagesReceived; }
    public Long getProjectClicks() { return projectClicks; }
    public void setProjectClicks(Long projectClicks) { this.projectClicks = projectClicks; }
}
