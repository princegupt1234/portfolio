package com.example.portfolio.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_project")
public class LearningProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String learningPath;

    @Column(columnDefinition = "TEXT")
    private String learningCards;

    private String status;
    private Integer sortOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getLearningPath() { return learningPath; }
    public void setLearningPath(String learningPath) { this.learningPath = learningPath; }
    public String getLearningCards() { return learningCards; }
    public void setLearningCards(String learningCards) { this.learningCards = learningCards; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
