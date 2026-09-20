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
    private Boolean availabilityVisible = true;

    private Boolean workPreferencesSectionVisible = true;

    @Column(columnDefinition = "TEXT")
    private String quickStats;
    private Boolean quickStatsVisible = true;

    @Column(columnDefinition = "TEXT")
    private String workPreference;
    private Boolean workPreferenceVisible = true;

    @Column(columnDefinition = "TEXT")
    private String preferredLocations;
    private Boolean preferredLocationsVisible = true;

    @Column(columnDefinition = "TEXT")
    private String languagesSpoken;
    private Boolean languagesSpokenVisible = true;

    @Column(columnDefinition = "TEXT")
    private String calendlyUrl;

    private Boolean terminalEnabled = true;
    @Column(columnDefinition = "TEXT")
    private String customCliCommands;

    @Column(columnDefinition = "TEXT")
    private String footerTagline;

    @Column(columnDefinition = "TEXT")
    private String footerSub;

    // ----- Master Frontend Section Visibility Toggles -----
    private Boolean sectionHeroVisible = true;
    private Boolean sectionQuickStatsVisible = true;
    private Boolean sectionAboutVisible = true;
    private Boolean sectionSkillsVisible = true;
    private Boolean sectionExperienceVisible = true;
    private Boolean sectionProjectsVisible = true;
    private Boolean sectionCodingVisible = true;
    private Boolean sectionCertificatesVisible = true;
    private Boolean sectionCurrentlyVisible = true;
    private Boolean sectionServicesVisible = true;
    private Boolean sectionTestimonialsVisible = true;
    private Boolean sectionContactVisible = true;

    // ----- Complete Hiring Availability Controls -----
    @Column(columnDefinition = "TEXT")
    private String hiringRoles;
    @Column(columnDefinition = "TEXT")
    private String hiringNoticePeriod;
    @Column(columnDefinition = "TEXT")
    private String hiringLocationDetails;
    @Column(columnDefinition = "TEXT")
    private String hiringContactEmail;
    @Column(columnDefinition = "TEXT")
    private String hiringCustomNote;

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

    // ----- Social Sharing & Open Graph (SEO / LinkedIn) Controls -----
    private Boolean ogTagsEnabled = true;
    @Column(columnDefinition = "TEXT")
    private String ogTitle;
    @Column(columnDefinition = "TEXT")
    private String ogDescription;
    @Column(columnDefinition = "TEXT")
    private String ogImageUrl;

    // ----- Contact Form Spam Protection & Rate Limiting -----
    private Boolean contactSpamProtectionEnabled = true;
    private Boolean contactHoneypotEnabled = true;
    private Integer contactRateLimitSeconds = 60;

    // ----- Admin WhatsApp Notification Alert Settings -----
    private Boolean whatsappNotificationEnabled = false;
    @Column(columnDefinition = "TEXT")
    private String whatsappNotificationPhone;
    @Column(columnDefinition = "TEXT")
    private String whatsappNotificationApiKey;
    @Column(columnDefinition = "TEXT")
    private String whatsappNotificationWebhookUrl;


    // ----- Outbound Email & Notification Settings (Admin Controlled) -----
    @Column(columnDefinition = "TEXT")
    private String mailNotificationEmail;
    @Column(columnDefinition = "TEXT")
    private String resendApiKey;
    @Column(columnDefinition = "TEXT")
    private String resendFrom;
    @Column(columnDefinition = "TEXT")
    private String brevoApiKey;
    @Column(columnDefinition = "TEXT")
    private String brevoSenderEmail;
    @Column(columnDefinition = "TEXT")
    private String brevoSenderName;
    @Column(columnDefinition = "TEXT")
    private String mailSendingMethod = "AUTO"; // AUTO, RESEND, BREVO, SMTP

    // ----- AI Chatbot & Gemini Credentials -----
    @Column(columnDefinition = "TEXT")
    private String geminiApiKey;
    @Column(columnDefinition = "TEXT")
    private String geminiModel = "gemini-1.5-flash";
    @Column(columnDefinition = "TEXT")
    private String aiCustomInstructions;

    // ----- LeetCode & Education Visibility -----
    @Column(columnDefinition = "TEXT")
    private String leetcodeUrl = "https://leetcode.com/u/princegupt1234/";
    private Boolean sectionEducationVisible = true;

    // ----- Mobile Web App & PWA Settings -----
    private Boolean pwaEnabled = true;
    private String pwaAppName = "Prince Gupt | Portfolio";
    private String pwaShortName = "Prince Portfolio";
    private String pwaThemeColor = "#0a0f1d";
    private String pwaBackgroundColor = "#060913";

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
    public Boolean getWorkPreferencesSectionVisible() { return workPreferencesSectionVisible; }
    public void setWorkPreferencesSectionVisible(Boolean workPreferencesSectionVisible) { this.workPreferencesSectionVisible = workPreferencesSectionVisible; }
    public String getQuickStats() { return quickStats; }
    public void setQuickStats(String quickStats) { this.quickStats = quickStats; }
    public Boolean getQuickStatsVisible() { return quickStatsVisible; }
    public void setQuickStatsVisible(Boolean quickStatsVisible) { this.quickStatsVisible = quickStatsVisible; }
    public String getWorkPreference() { return workPreference; }
    public void setWorkPreference(String workPreference) { this.workPreference = workPreference; }
    public Boolean getWorkPreferenceVisible() { return workPreferenceVisible; }
    public void setWorkPreferenceVisible(Boolean workPreferenceVisible) { this.workPreferenceVisible = workPreferenceVisible; }
    public String getPreferredLocations() { return preferredLocations; }
    public void setPreferredLocations(String preferredLocations) { this.preferredLocations = preferredLocations; }
    public Boolean getPreferredLocationsVisible() { return preferredLocationsVisible; }
    public void setPreferredLocationsVisible(Boolean preferredLocationsVisible) { this.preferredLocationsVisible = preferredLocationsVisible; }
    public String getLanguagesSpoken() { return languagesSpoken; }
    public void setLanguagesSpoken(String languagesSpoken) { this.languagesSpoken = languagesSpoken; }
    public Boolean getLanguagesSpokenVisible() { return languagesSpokenVisible; }
    public void setLanguagesSpokenVisible(Boolean languagesSpokenVisible) { this.languagesSpokenVisible = languagesSpokenVisible; }
    public String getCalendlyUrl() { return calendlyUrl; }
    public void setCalendlyUrl(String calendlyUrl) { this.calendlyUrl = calendlyUrl; }
    public Boolean getTerminalEnabled() { return terminalEnabled; }
    public void setTerminalEnabled(Boolean terminalEnabled) { this.terminalEnabled = terminalEnabled; }
    public String getCustomCliCommands() { return customCliCommands; }
    public void setCustomCliCommands(String customCliCommands) { this.customCliCommands = customCliCommands; }
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

    public Boolean getOgTagsEnabled() { return ogTagsEnabled; }
    public void setOgTagsEnabled(Boolean ogTagsEnabled) { this.ogTagsEnabled = ogTagsEnabled; }
    public String getOgTitle() { return ogTitle; }
    public void setOgTitle(String ogTitle) { this.ogTitle = ogTitle; }
    public String getOgDescription() { return ogDescription; }
    public void setOgDescription(String ogDescription) { this.ogDescription = ogDescription; }
    public String getOgImageUrl() { return ogImageUrl; }
    public void setOgImageUrl(String ogImageUrl) { this.ogImageUrl = ogImageUrl; }

    public Boolean getContactSpamProtectionEnabled() { return contactSpamProtectionEnabled; }
    public void setContactSpamProtectionEnabled(Boolean contactSpamProtectionEnabled) { this.contactSpamProtectionEnabled = contactSpamProtectionEnabled; }
    public Boolean getContactHoneypotEnabled() { return contactHoneypotEnabled; }
    public void setContactHoneypotEnabled(Boolean contactHoneypotEnabled) { this.contactHoneypotEnabled = contactHoneypotEnabled; }
    public Integer getContactRateLimitSeconds() { return contactRateLimitSeconds; }
    public void setContactRateLimitSeconds(Integer contactRateLimitSeconds) { this.contactRateLimitSeconds = contactRateLimitSeconds; }

    public Boolean getPwaEnabled() { return pwaEnabled; }
    public void setPwaEnabled(Boolean pwaEnabled) { this.pwaEnabled = pwaEnabled; }
    public String getPwaAppName() { return pwaAppName; }
    public void setPwaAppName(String pwaAppName) { this.pwaAppName = pwaAppName; }
    public String getPwaShortName() { return pwaShortName; }
    public void setPwaShortName(String pwaShortName) { this.pwaShortName = pwaShortName; }
    public String getPwaThemeColor() { return pwaThemeColor; }
    public void setPwaThemeColor(String pwaThemeColor) { this.pwaThemeColor = pwaThemeColor; }
    public String getPwaBackgroundColor() { return pwaBackgroundColor; }
    public void setPwaBackgroundColor(String pwaBackgroundColor) { this.pwaBackgroundColor = pwaBackgroundColor; }

    public String getHiringRoles() { return hiringRoles; }
    public void setHiringRoles(String hiringRoles) { this.hiringRoles = hiringRoles; }
    public String getHiringNoticePeriod() { return hiringNoticePeriod; }
    public void setHiringNoticePeriod(String hiringNoticePeriod) { this.hiringNoticePeriod = hiringNoticePeriod; }
    public String getHiringLocationDetails() { return hiringLocationDetails; }
    public void setHiringLocationDetails(String hiringLocationDetails) { this.hiringLocationDetails = hiringLocationDetails; }
    public String getHiringContactEmail() { return hiringContactEmail; }
    public void setHiringContactEmail(String hiringContactEmail) { this.hiringContactEmail = hiringContactEmail; }
    public String getHiringCustomNote() { return hiringCustomNote; }
    public void setHiringCustomNote(String hiringCustomNote) { this.hiringCustomNote = hiringCustomNote; }

    public Boolean getSectionHeroVisible() { return sectionHeroVisible != null ? sectionHeroVisible : true; }
    public void setSectionHeroVisible(Boolean sectionHeroVisible) { this.sectionHeroVisible = sectionHeroVisible; }
    public Boolean getSectionQuickStatsVisible() { return sectionQuickStatsVisible != null ? sectionQuickStatsVisible : true; }
    public void setSectionQuickStatsVisible(Boolean sectionQuickStatsVisible) { this.sectionQuickStatsVisible = sectionQuickStatsVisible; }
    public Boolean getSectionAboutVisible() { return sectionAboutVisible != null ? sectionAboutVisible : true; }
    public void setSectionAboutVisible(Boolean sectionAboutVisible) { this.sectionAboutVisible = sectionAboutVisible; }
    public Boolean getSectionSkillsVisible() { return sectionSkillsVisible != null ? sectionSkillsVisible : true; }
    public void setSectionSkillsVisible(Boolean sectionSkillsVisible) { this.sectionSkillsVisible = sectionSkillsVisible; }
    public Boolean getSectionExperienceVisible() { return sectionExperienceVisible != null ? sectionExperienceVisible : true; }
    public void setSectionExperienceVisible(Boolean sectionExperienceVisible) { this.sectionExperienceVisible = sectionExperienceVisible; }
    public Boolean getSectionProjectsVisible() { return sectionProjectsVisible != null ? sectionProjectsVisible : true; }
    public void setSectionProjectsVisible(Boolean sectionProjectsVisible) { this.sectionProjectsVisible = sectionProjectsVisible; }
    public Boolean getSectionCodingVisible() { return sectionCodingVisible != null ? sectionCodingVisible : true; }
    public void setSectionCodingVisible(Boolean sectionCodingVisible) { this.sectionCodingVisible = sectionCodingVisible; }
    public Boolean getSectionCertificatesVisible() { return sectionCertificatesVisible != null ? sectionCertificatesVisible : true; }
    public void setSectionCertificatesVisible(Boolean sectionCertificatesVisible) { this.sectionCertificatesVisible = sectionCertificatesVisible; }
    public Boolean getSectionCurrentlyVisible() { return sectionCurrentlyVisible != null ? sectionCurrentlyVisible : true; }
    public void setSectionCurrentlyVisible(Boolean sectionCurrentlyVisible) { this.sectionCurrentlyVisible = sectionCurrentlyVisible; }
    public Boolean getSectionServicesVisible() { return sectionServicesVisible != null ? sectionServicesVisible : true; }
    public void setSectionServicesVisible(Boolean sectionServicesVisible) { this.sectionServicesVisible = sectionServicesVisible; }
    public Boolean getSectionTestimonialsVisible() { return sectionTestimonialsVisible != null ? sectionTestimonialsVisible : true; }
    public void setSectionTestimonialsVisible(Boolean sectionTestimonialsVisible) { this.sectionTestimonialsVisible = sectionTestimonialsVisible; }
    public Boolean getSectionContactVisible() { return sectionContactVisible != null ? sectionContactVisible : true; }
    public void setSectionContactVisible(Boolean sectionContactVisible) { this.sectionContactVisible = sectionContactVisible; }

    public Boolean getWhatsappNotificationEnabled() { return whatsappNotificationEnabled != null ? whatsappNotificationEnabled : false; }
    public void setWhatsappNotificationEnabled(Boolean whatsappNotificationEnabled) { this.whatsappNotificationEnabled = whatsappNotificationEnabled; }
    public String getWhatsappNotificationPhone() { return whatsappNotificationPhone; }
    public void setWhatsappNotificationPhone(String whatsappNotificationPhone) { this.whatsappNotificationPhone = whatsappNotificationPhone; }
    public String getWhatsappNotificationApiKey() { return whatsappNotificationApiKey; }
    public void setWhatsappNotificationApiKey(String whatsappNotificationApiKey) { this.whatsappNotificationApiKey = whatsappNotificationApiKey; }
    public String getWhatsappNotificationWebhookUrl() { return whatsappNotificationWebhookUrl; }
    public void setWhatsappNotificationWebhookUrl(String whatsappNotificationWebhookUrl) { this.whatsappNotificationWebhookUrl = whatsappNotificationWebhookUrl; }


    public String getMailNotificationEmail() { return mailNotificationEmail; }
    public void setMailNotificationEmail(String mailNotificationEmail) { this.mailNotificationEmail = mailNotificationEmail; }
    public String getResendApiKey() { return resendApiKey; }
    public void setResendApiKey(String resendApiKey) { this.resendApiKey = resendApiKey; }
    public String getResendFrom() { return resendFrom; }
    public void setResendFrom(String resendFrom) { this.resendFrom = resendFrom; }
    public String getBrevoApiKey() { return brevoApiKey; }
    public void setBrevoApiKey(String brevoApiKey) { this.brevoApiKey = brevoApiKey; }
    public String getBrevoSenderEmail() { return brevoSenderEmail; }
    public void setBrevoSenderEmail(String brevoSenderEmail) { this.brevoSenderEmail = brevoSenderEmail; }
    public String getBrevoSenderName() { return brevoSenderName; }
    public void setBrevoSenderName(String brevoSenderName) { this.brevoSenderName = brevoSenderName; }
    public String getMailSendingMethod() { return mailSendingMethod != null && !mailSendingMethod.isBlank() ? mailSendingMethod : "AUTO"; }
    public void setMailSendingMethod(String mailSendingMethod) { this.mailSendingMethod = mailSendingMethod; }

    public String getGeminiApiKey() { return geminiApiKey; }
    public void setGeminiApiKey(String geminiApiKey) { this.geminiApiKey = geminiApiKey; }
    public String getGeminiModel() { return geminiModel != null && !geminiModel.isBlank() ? geminiModel : "gemini-1.5-flash"; }
    public void setGeminiModel(String geminiModel) { this.geminiModel = geminiModel; }
    public String getAiCustomInstructions() { return aiCustomInstructions; }
    public void setAiCustomInstructions(String aiCustomInstructions) { this.aiCustomInstructions = aiCustomInstructions; }

    public String getLeetcodeUrl() { return leetcodeUrl != null && !leetcodeUrl.isBlank() ? leetcodeUrl : "https://leetcode.com/u/princegupt1234/"; }
    public void setLeetcodeUrl(String leetcodeUrl) { this.leetcodeUrl = leetcodeUrl; }
    public Boolean getSectionEducationVisible() { return sectionEducationVisible != null ? sectionEducationVisible : true; }
    public void setSectionEducationVisible(Boolean sectionEducationVisible) { this.sectionEducationVisible = sectionEducationVisible; }
}
