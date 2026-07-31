package com.example.portfolio.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "about_info")
public class AboutInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String title;
    private String heroEyebrow;
    private String heroPrimaryCtaLabel;
    private String heroPrimaryCtaLink;
    private String heroSecondaryCtaLabel;
    private String heroSecondaryCtaLink;
    private String heroBackground;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHeroEyebrow() { return heroEyebrow; }
    public void setHeroEyebrow(String heroEyebrow) { this.heroEyebrow = heroEyebrow; }
    public String getHeroPrimaryCtaLabel() { return heroPrimaryCtaLabel; }
    public void setHeroPrimaryCtaLabel(String heroPrimaryCtaLabel) { this.heroPrimaryCtaLabel = heroPrimaryCtaLabel; }
    public String getHeroPrimaryCtaLink() { return heroPrimaryCtaLink; }
    public void setHeroPrimaryCtaLink(String heroPrimaryCtaLink) { this.heroPrimaryCtaLink = heroPrimaryCtaLink; }
    public String getHeroSecondaryCtaLabel() { return heroSecondaryCtaLabel; }
    public void setHeroSecondaryCtaLabel(String heroSecondaryCtaLabel) { this.heroSecondaryCtaLabel = heroSecondaryCtaLabel; }
    public String getHeroSecondaryCtaLink() { return heroSecondaryCtaLink; }
    public void setHeroSecondaryCtaLink(String heroSecondaryCtaLink) { this.heroSecondaryCtaLink = heroSecondaryCtaLink; }
    public String getHeroBackground() { return heroBackground; }
    public void setHeroBackground(String heroBackground) { this.heroBackground = heroBackground; }
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
}
