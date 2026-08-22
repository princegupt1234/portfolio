package com.example.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "about_info")
public class AboutInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String title;
    private String heroEyebrow;
    private Integer heroTypingSpeed;
    private Integer heroDeletingSpeed;
    private Integer heroPauseDuration;

    @Column(length = 2000)
    private String heroPhrases;

    @Column(length = 2000)
    private String bio;

    @Column(length = 2000)
    private String careerObjective;

    private String profileImage;
    private String resumeUrl;
    private String phone;
    private String email;
    private String location;

    private String githubUrl;
    private String linkedinUrl;
    private String whatsappUrl;
    private String leetcodeUsername;
    private String githubUsername;

    @Column(length = 500)
    private String heroTechStack; // pipe-separated e.g. Java|Spring Boot|REST APIs|MySQL|React

    @Column(length = 500)
    private String buildingProjectTitle;

    @Column(length = 500)
    private String buildingProjectDesc;

    @Column(length = 500)
    private String buildingTechStack; // pipe-separated

    private String buildingProjectUrl;

    private Integer buildingProgress; // 0-100

    private String buildingStatus; // e.g. "In Progress", "Beta", "Live"

    @Column(length = 1000)
    private String currentlyLearning;

    @Column(columnDefinition = "TEXT")
    private String learningCards;

    @Column(length = 500)
    private String contactMessage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHeroEyebrow() { return heroEyebrow; }
    public void setHeroEyebrow(String heroEyebrow) { this.heroEyebrow = heroEyebrow; }
    public Integer getHeroTypingSpeed() { return heroTypingSpeed; }
    public void setHeroTypingSpeed(Integer heroTypingSpeed) { this.heroTypingSpeed = heroTypingSpeed; }
    public Integer getHeroDeletingSpeed() { return heroDeletingSpeed; }
    public void setHeroDeletingSpeed(Integer heroDeletingSpeed) { this.heroDeletingSpeed = heroDeletingSpeed; }
    public Integer getHeroPauseDuration() { return heroPauseDuration; }
    public void setHeroPauseDuration(Integer heroPauseDuration) { this.heroPauseDuration = heroPauseDuration; }
    public String getHeroPhrases() { return heroPhrases; }
    public void setHeroPhrases(String heroPhrases) { this.heroPhrases = heroPhrases; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCareerObjective() { return careerObjective; }
    public void setCareerObjective(String careerObjective) { this.careerObjective = careerObjective; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getWhatsappUrl() { return whatsappUrl; }
    public void setWhatsappUrl(String whatsappUrl) { this.whatsappUrl = whatsappUrl; }
    public String getLeetcodeUsername() { return leetcodeUsername; }
    public void setLeetcodeUsername(String leetcodeUsername) { this.leetcodeUsername = leetcodeUsername; }
    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }
    public String getHeroTechStack() { return heroTechStack; }
    public void setHeroTechStack(String heroTechStack) { this.heroTechStack = heroTechStack; }
    public String getBuildingProjectTitle() { return buildingProjectTitle; }
    public void setBuildingProjectTitle(String buildingProjectTitle) { this.buildingProjectTitle = buildingProjectTitle; }
    public String getBuildingProjectDesc() { return buildingProjectDesc; }
    public void setBuildingProjectDesc(String buildingProjectDesc) { this.buildingProjectDesc = buildingProjectDesc; }
    public String getBuildingTechStack() { return buildingTechStack; }
    public void setBuildingTechStack(String buildingTechStack) { this.buildingTechStack = buildingTechStack; }
    public String getBuildingProjectUrl() { return buildingProjectUrl; }
    public void setBuildingProjectUrl(String buildingProjectUrl) { this.buildingProjectUrl = buildingProjectUrl; }
    public Integer getBuildingProgress() { return buildingProgress; }
    public void setBuildingProgress(Integer buildingProgress) { this.buildingProgress = buildingProgress; }
    public String getBuildingStatus() { return buildingStatus; }
    public void setBuildingStatus(String buildingStatus) { this.buildingStatus = buildingStatus; }
    public String getCurrentlyLearning() { return currentlyLearning; }
    public void setCurrentlyLearning(String currentlyLearning) { this.currentlyLearning = currentlyLearning; }
    public String getLearningCards() { return learningCards; }
    public void setLearningCards(String learningCards) { this.learningCards = learningCards; }
    public String getContactMessage() { return contactMessage; }
    public void setContactMessage(String contactMessage) { this.contactMessage = contactMessage; }
}
