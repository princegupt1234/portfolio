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

    @Column(columnDefinition = "TEXT")
    private String fullName;
    @Column(columnDefinition = "TEXT")
    private String title;
    @Column(columnDefinition = "TEXT")
    private String heroEyebrow;
    private Integer heroTypingSpeed;
    private Integer heroDeletingSpeed;
    private Integer heroPauseDuration;

    @Column(columnDefinition = "TEXT")
    private String heroPrimaryCtaLabel;

    @Column(columnDefinition = "TEXT")
    private String heroPrimaryCtaLink;

    @Column(columnDefinition = "TEXT")
    private String heroSecondaryCtaLabel;

    @Column(columnDefinition = "TEXT")
    private String heroSecondaryCtaLink;

    @Column(columnDefinition = "TEXT")
    private String heroPhrases;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String careerObjective;

    @Column(columnDefinition = "TEXT")
    private String profileImage;
    @Column(columnDefinition = "TEXT")
    private String resumeUrl;
    @Column(columnDefinition = "TEXT")
    private String phone;
    @Column(columnDefinition = "TEXT")
    private String email;
    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(columnDefinition = "TEXT")
    private String githubUrl;
    @Column(columnDefinition = "TEXT")
    private String linkedinUrl;
    @Column(columnDefinition = "TEXT")
    private String whatsappUrl;
    @Column(columnDefinition = "TEXT")
    private String githubUsername;

    @Column(columnDefinition = "TEXT")
    private String heroTechStack; // pipe-separated e.g. Java|Spring Boot|REST APIs|MySQL|React

    @Column(columnDefinition = "TEXT")
    private String buildingProjectTitle;

    @Column(columnDefinition = "TEXT")
    private String buildingProjectDesc;

    @Column(columnDefinition = "TEXT")
    private String buildingTechStack; // pipe-separated

    @Column(columnDefinition = "TEXT")
    private String currentlyBuilding;

    @Column(columnDefinition = "TEXT")
    private String buildingProjectUrl;

    private Integer buildingProgress; // 0-100

    @Column(columnDefinition = "TEXT")
    private String buildingStatus; // e.g. "In Progress", "Beta", "Live"

    @Column(columnDefinition = "TEXT")
    private String currentlyLearning;

    @Column(columnDefinition = "TEXT")
    private String learningPath;

    @Column(columnDefinition = "TEXT")
    private String learningCards;

    @Column(columnDefinition = "TEXT")
    private String contactMessage;

    @Column(columnDefinition = "TEXT")
    private String availabilityText;
    private Boolean availabilityVisible = false;

    @Column(columnDefinition = "TEXT")
    private String quickStats;
    private Boolean quickStatsVisible = true;

    @Column(columnDefinition = "TEXT")
    private String workPreference;

    @Column(columnDefinition = "TEXT")
    private String preferredLocations;

    @Column(columnDefinition = "TEXT")
    private String languagesSpoken;

    @Column(columnDefinition = "TEXT")
    private String calendlyUrl;

    private Boolean terminalEnabled = true;

    @Column(columnDefinition = "TEXT")
    private String footerTagline;

    @Column(columnDefinition = "TEXT")
    private String footerSub;

    // ----- Recruiter 30-Second Pitch Modal Controls -----
    private Boolean recruiterPitchEnabled = true;
    @Column(columnDefinition = "TEXT")
    private String recruiterTargetRole;
    @Column(columnDefinition = "TEXT")
    private String recruiterMetrics;
    @Column(columnDefinition = "TEXT")
    private String recruiterHighlights;
    @Column(columnDefinition = "TEXT")
    private String recruiterPitchCopyText;

    // ----- "Ask Prince AI" Chatbot Controls -----
    private Boolean aiChatEnabled = true;
    @Column(columnDefinition = "TEXT")
    private String aiChatWelcomeMessage;
    @Column(columnDefinition = "TEXT")
    private String aiChatPromptChips;

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
    public String getHeroPrimaryCtaLabel() { return heroPrimaryCtaLabel; }
    public void setHeroPrimaryCtaLabel(String heroPrimaryCtaLabel) { this.heroPrimaryCtaLabel = heroPrimaryCtaLabel; }
    public String getHeroPrimaryCtaLink() { return heroPrimaryCtaLink; }
    public void setHeroPrimaryCtaLink(String heroPrimaryCtaLink) { this.heroPrimaryCtaLink = heroPrimaryCtaLink; }
    public String getHeroSecondaryCtaLabel() { return heroSecondaryCtaLabel; }
    public void setHeroSecondaryCtaLabel(String heroSecondaryCtaLabel) { this.heroSecondaryCtaLabel = heroSecondaryCtaLabel; }
    public String getHeroSecondaryCtaLink() { return heroSecondaryCtaLink; }
    public void setHeroSecondaryCtaLink(String heroSecondaryCtaLink) { this.heroSecondaryCtaLink = heroSecondaryCtaLink; }
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
    public String getCurrentlyBuilding() { return currentlyBuilding; }
    public void setCurrentlyBuilding(String currentlyBuilding) { this.currentlyBuilding = currentlyBuilding; }
    public String getBuildingProjectUrl() { return buildingProjectUrl; }
    public void setBuildingProjectUrl(String buildingProjectUrl) { this.buildingProjectUrl = buildingProjectUrl; }
    public Integer getBuildingProgress() { return buildingProgress; }
    public void setBuildingProgress(Integer buildingProgress) { this.buildingProgress = buildingProgress; }
    public String getBuildingStatus() { return buildingStatus; }
    public void setBuildingStatus(String buildingStatus) { this.buildingStatus = buildingStatus; }
    public String getCurrentlyLearning() { return currentlyLearning; }
    public void setCurrentlyLearning(String currentlyLearning) { this.currentlyLearning = currentlyLearning; }
    public String getLearningPath() { return learningPath; }
    public void setLearningPath(String learningPath) { this.learningPath = learningPath; }
    public String getLearningCards() { return learningCards; }
    public void setLearningCards(String learningCards) { this.learningCards = learningCards; }
    public String getContactMessage() { return contactMessage; }
    public void setContactMessage(String contactMessage) { this.contactMessage = contactMessage; }
    public String getAvailabilityText() { return availabilityText; }
    public void setAvailabilityText(String availabilityText) { this.availabilityText = availabilityText; }
    public Boolean getAvailabilityVisible() { return availabilityVisible; }
    public void setAvailabilityVisible(Boolean availabilityVisible) { this.availabilityVisible = availabilityVisible; }
    public String getQuickStats() { return quickStats; }
    public void setQuickStats(String quickStats) { this.quickStats = quickStats; }
    public Boolean getQuickStatsVisible() { return quickStatsVisible; }
    public void setQuickStatsVisible(Boolean quickStatsVisible) { this.quickStatsVisible = quickStatsVisible; }
    public String getWorkPreference() { return workPreference; }
    public void setWorkPreference(String workPreference) { this.workPreference = workPreference; }
    public String getPreferredLocations() { return preferredLocations; }
    public void setPreferredLocations(String preferredLocations) { this.preferredLocations = preferredLocations; }
    public String getLanguagesSpoken() { return languagesSpoken; }
    public void setLanguagesSpoken(String languagesSpoken) { this.languagesSpoken = languagesSpoken; }
    public String getCalendlyUrl() { return calendlyUrl; }
    public void setCalendlyUrl(String calendlyUrl) { this.calendlyUrl = calendlyUrl; }
    public Boolean getTerminalEnabled() { return terminalEnabled; }
    public void setTerminalEnabled(Boolean terminalEnabled) { this.terminalEnabled = terminalEnabled; }
    public String getFooterTagline() { return footerTagline; }
    public void setFooterTagline(String footerTagline) { this.footerTagline = footerTagline; }
    public String getFooterSub() { return footerSub; }
    public void setFooterSub(String footerSub) { this.footerSub = footerSub; }

    public Boolean getRecruiterPitchEnabled() { return recruiterPitchEnabled; }
    public void setRecruiterPitchEnabled(Boolean recruiterPitchEnabled) { this.recruiterPitchEnabled = recruiterPitchEnabled; }
    public String getRecruiterTargetRole() { return recruiterTargetRole; }
    public void setRecruiterTargetRole(String recruiterTargetRole) { this.recruiterTargetRole = recruiterTargetRole; }
    public String getRecruiterMetrics() { return recruiterMetrics; }
    public void setRecruiterMetrics(String recruiterMetrics) { this.recruiterMetrics = recruiterMetrics; }
    public String getRecruiterHighlights() { return recruiterHighlights; }
    public void setRecruiterHighlights(String recruiterHighlights) { this.recruiterHighlights = recruiterHighlights; }
    public String getRecruiterPitchCopyText() { return recruiterPitchCopyText; }
    public void setRecruiterPitchCopyText(String recruiterPitchCopyText) { this.recruiterPitchCopyText = recruiterPitchCopyText; }

    public Boolean getAiChatEnabled() { return aiChatEnabled; }
    public void setAiChatEnabled(Boolean aiChatEnabled) { this.aiChatEnabled = aiChatEnabled; }
    public String getAiChatWelcomeMessage() { return aiChatWelcomeMessage; }
    public void setAiChatWelcomeMessage(String aiChatWelcomeMessage) { this.aiChatWelcomeMessage = aiChatWelcomeMessage; }
    public String getAiChatPromptChips() { return aiChatPromptChips; }
    public void setAiChatPromptChips(String aiChatPromptChips) { this.aiChatPromptChips = aiChatPromptChips; }
}
