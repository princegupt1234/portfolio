package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SiteStatRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PortfolioAiService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioAiService.class);
    private static final String PORTFOLIO_HOST_URL = "https://portfolio-gx88.onrender.com";

    private final AboutInfoRepository aboutInfoRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final CertificateRepository certificateRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final TestimonialRepository testimonialRepository;
    private final SiteStatRepository siteStatRepository;
    private final AiTrainingRepository aiTrainingRepository;
    private final LeetCodeRepositoryStatsService leetCodeRepositoryStatsService;
    private final RestClient restClient;

    @Value("${gemini.api.key:}")
    private String configuredGeminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String configuredGeminiModel;

    public record AiAnswerResult(String reply, String source, String model) {}

    public PortfolioAiService(AboutInfoRepository aboutInfoRepository,
                              SkillRepository skillRepository,
                              ProjectRepository projectRepository,
                              ExperienceRepository experienceRepository,
                              EducationEntryRepository educationEntryRepository,
                              BuildingProjectRepository buildingProjectRepository,
                              LearningProjectRepository learningProjectRepository,
                              CertificateRepository certificateRepository,
                              ServiceItemRepository serviceItemRepository,
                              TestimonialRepository testimonialRepository,
                              SiteStatRepository siteStatRepository,
                              AiTrainingRepository aiTrainingRepository,
                              LeetCodeRepositoryStatsService leetCodeRepositoryStatsService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.certificateRepository = certificateRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.siteStatRepository = siteStatRepository;
        this.aiTrainingRepository = aiTrainingRepository;
        this.leetCodeRepositoryStatsService = leetCodeRepositoryStatsService;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(8));
        requestFactory.setReadTimeout(Duration.ofSeconds(25));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public String answer(String userQuery) {
        return answerWithDetails(userQuery).reply();
    }

    public AiAnswerResult answerWithDetails(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return new AiAnswerResult(
                    "Please feel free to ask any question about Prince's skills, projects, experience, services, or hiring availability!",
                    "DEFAULT_PROMPT",
                    "Assistant"
            );
        }

        String query = userQuery.trim();

        // 1. Check Active Admin-Trained Knowledge Overrides (Highest Priority Ground Truth)
        AiTraining trainedMatch = findTrainedOverride(query);
        if (trainedMatch != null && trainedMatch.getCorrectAnswer() != null && !trainedMatch.getCorrectAnswer().isBlank()) {
            log.info("AI Query matched active admin training rule #{}: '{}'", trainedMatch.getId(), trainedMatch.getQuestionTrigger());
            return new AiAnswerResult(
                    trainedMatch.getCorrectAnswer().trim(),
                    "TRAINED_OVERRIDE",
                    "Trained Knowledge Base (" + (trainedMatch.getCategory() != null ? trainedMatch.getCategory() : "Custom") + ")"
            );
        }

        // 2. Check configured Gemini API key (Admin panel, properties, or OS environment variables)
        String effectiveKey = resolveGeminiApiKey();
        if (effectiveKey != null && !effectiveKey.isBlank()) {
            try {
                GeminiResponse geminiRes = callGeminiApi(query, effectiveKey.trim());
                if (geminiRes != null && geminiRes.text() != null && !geminiRes.text().isBlank()) {
                    return new AiAnswerResult(geminiRes.text().trim(), "GEMINI_API", geminiRes.model());
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed (falling back to local portfolio domain engine): {}", ex.getMessage());
            }
        }

        // 3. Fallback intelligent domain engine using live database context
        String localReply = buildLocalFallbackResponse(query);
        return new AiAnswerResult(localReply, "LOCAL_ENGINE", "Portfolio Intelligent Domain Engine");
    }

    public String resolveGeminiApiKey() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
        if (about != null && about.getGeminiApiKey() != null && !about.getGeminiApiKey().isBlank()) {
            return about.getGeminiApiKey().trim();
        }
        if (configuredGeminiApiKey != null && !configuredGeminiApiKey.trim().isEmpty()) {
            return configuredGeminiApiKey.trim();
        }
        for (String envName : List.of("GEMINI_API_KEY", "GEMINI_API", "GIMINI_API", "GIMINI_API_KEY", "GEMINI_KEY", "GOOGLE_API_KEY")) {
            String val = System.getenv(envName);
            if (val != null && !val.trim().isEmpty()) {
                return val.trim();
            }
        }
        return null;
    }

    public String resolveGeminiModel() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
        if (about != null && about.getGeminiModel() != null && !about.getGeminiModel().isBlank()) {
            return about.getGeminiModel().trim();
        }
        if (configuredGeminiModel != null && !configuredGeminiModel.isBlank()) {
            return configuredGeminiModel.trim();
        }
        return "gemini-2.5-flash";
    }

    public boolean isGeminiConfigured() {
        String key = resolveGeminiApiKey();
        return key != null && !key.isBlank();
    }

    public AiTraining findTrainedOverride(String userQuery) {
        if (userQuery == null || userQuery.isBlank()) return null;
        List<AiTraining> activeRules = aiTrainingRepository.findByActiveTrueOrderBySortOrderAscIdDesc();
        if (activeRules.isEmpty()) return null;

        String cleanQuery = userQuery.trim().toLowerCase(Locale.ROOT);
        String normQuery = cleanQuery.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();

        // 1. Direct or substring match on question trigger
        for (AiTraining rule : activeRules) {
            if (rule.getQuestionTrigger() == null || rule.getQuestionTrigger().isBlank()) continue;
            String normTrigger = rule.getQuestionTrigger().trim().toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();

            if (normQuery.equals(normTrigger) || normQuery.contains(normTrigger) || (normTrigger.length() >= 6 && normTrigger.contains(normQuery))) {
                return rule;
            }
        }

        // 2. Keyword trigger match if comma-separated keywords defined
        for (AiTraining rule : activeRules) {
            if (rule.getMatchKeywords() == null || rule.getMatchKeywords().isBlank()) continue;
            String[] keywords = rule.getMatchKeywords().split(",");
            for (String kw : keywords) {
                String k = kw.trim().toLowerCase(Locale.ROOT);
                if (!k.isEmpty() && normQuery.matches(".*\\b" + Pattern.quote(k) + "\\b.*")) {
                    return rule;
                }
            }
        }

        // 3. Significant word overlap match (>= 75% of non-trivial words)
        for (AiTraining rule : activeRules) {
            if (rule.getQuestionTrigger() == null || rule.getQuestionTrigger().isBlank()) continue;
            String normTrigger = rule.getQuestionTrigger().trim().toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
            String[] triggerWords = normTrigger.split("\\s+");
            int nonTrivialCount = 0;
            int matches = 0;
            for (String tw : triggerWords) {
                if (tw.length() <= 2) continue; // skip 'is', 'a', 'to'
                nonTrivialCount++;
                if (normQuery.contains(tw)) {
                    matches++;
                }
            }
            if (nonTrivialCount >= 2 && ((double) matches / nonTrivialCount) >= 0.75) {
                return rule;
            }
        }

        return null;
    }

    private record GeminiResponse(String text, String model) {}

    private GeminiResponse callGeminiApi(String userQuery, String apiKey) {
        String systemContext = buildPortfolioContext();
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());

        String customPromptNote = (about.getAiCustomInstructions() != null && !about.getAiCustomInstructions().isBlank())
                ? "\nADDITIONAL ADMIN INSTRUCTIONS:\n" + about.getAiCustomInstructions().trim() + "\n"
                : "";

        String prompt = "You are the official interactive AI portfolio assistant for Prince Gupt, hosted on his live portfolio website: " + PORTFOLIO_HOST_URL + ".\n\n"
                + "MISSION:\n"
                + "Represent Prince accurately, politely, and professionally to recruiters, engineering managers, and visitors.\n\n"
                + "CRITICAL RULES:\n"
                + "1. STRICT TRUTH: Answer all questions based strictly on the verified portfolio data provided below. Do NOT hallucinate or exaggerate skills, company names, projects, or degrees.\n"
                + "2. ADMIN TRAINED OVERRIDES: If a verified admin training answer exists for this topic below, you MUST prioritize and adhere to it completely.\n"
                + "3. LEETCODE & DSA: State Prince's actual solved count and focus areas from the data. Never claim fabricated numbers or 350+.\n"
                + "4. PROJECTS & SERVICES: Talk about his real projects (POS Billing System, Travel Booking Website, Personal Portfolio, etc.) with real tech stacks (Spring Boot, MySQL, React, etc.) and real services he provides.\n"
                + "5. HIRING & AVAILABILITY: State his immediate joining availability (0 days notice) and provide his contact email.\n"
                + "6. FORMATTING: Use crisp markdown with bullet points where appropriate. Keep responses engaging, courteous, and concise (under 140 words).\n"
                + customPromptNote + "\n"
                + "=== PRINCE GUPT COMPLETE PORTFOLIO KNOWLEDGE BASE (HOSTED ON " + PORTFOLIO_HOST_URL + ") ===\n"
                + systemContext + "\n"
                + "=== END OF PORTFOLIO KNOWLEDGE BASE ===\n\n"
                + "Visitor Question: " + userQuery + "\n\n"
                + "Answer:";

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentObj = Map.of("role", "user", "parts", List.of(textPart));
        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.6,
                "maxOutputTokens", 1024
        );
        Map<String, Object> body = Map.of(
                "contents", List.of(contentObj),
                "generationConfig", generationConfig
        );

        String preferredModel = resolveGeminiModel();
        List<String> models = List.of(
                preferredModel,
                "gemini-2.5-flash",
                "gemini-2.0-flash",
                "gemini-1.5-flash"
        ).stream().filter(m -> m != null && !m.isBlank()).distinct().toList();

        for (String model : models) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

                Map<?, ?> responseMap = restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);

                if (responseMap != null && responseMap.get("candidates") instanceof List<?> candidates && !candidates.isEmpty()) {
                    Object firstCand = candidates.get(0);
                    if (firstCand instanceof Map<?, ?> candMap && candMap.get("content") instanceof Map<?, ?> content) {
                        if (content.get("parts") instanceof List<?> parts && !parts.isEmpty()) {
                            // Extract actual text while ignoring thinking reasoning parts
                            StringBuilder textAcc = new StringBuilder();
                            for (Object p : parts) {
                                if (p instanceof Map<?, ?> partMap) {
                                    Object thoughtVal = partMap.get("thought");
                                    boolean isThought = Boolean.TRUE.equals(thoughtVal)
                                            || (thoughtVal instanceof String && !((String) thoughtVal).isBlank());
                                    if (!isThought && partMap.get("text") instanceof String text) {
                                        textAcc.append(text);
                                    }
                                }
                            }
                            if (!textAcc.isEmpty()) {
                                return new GeminiResponse(textAcc.toString().trim(), model);
                            }
                            // Fallback if parts didn't have thought flag
                            for (Object p : parts) {
                                if (p instanceof Map<?, ?> partMap && partMap.get("text") instanceof String text && !text.isBlank()) {
                                    textAcc.append(text);
                                }
                            }
                            if (!textAcc.isEmpty()) {
                                return new GeminiResponse(textAcc.toString().trim(), model);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.debug("Gemini model '{}' attempt failed: {}", model, ex.getMessage());
            }
        }
        return null;
    }

    public String buildPortfolioContext() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
        List<Project> projects = projectRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<Experience> experiences = experienceRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<EducationEntry> education = educationEntryRepository.findAllByOrderBySortOrderAsc();
        List<BuildingProject> buildingProjects = buildingProjectRepository.findAllByOrderBySortOrderAsc();
        List<LearningProject> learningProjects = learningProjectRepository.findAllByOrderBySortOrderAsc();
        List<Certificate> certificates = certificateRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<ServiceItem> services = serviceItemRepository.findAllByOrderBySortOrderAsc();
        List<Testimonial> testimonials = testimonialRepository.findByPublishedTrue();
        List<SiteStat> siteStats = siteStatRepository.findAll();
        List<AiTraining> activeTraining = aiTrainingRepository.findByActiveTrueOrderBySortOrderAscIdDesc();

        StringBuilder sb = new StringBuilder();
        sb.append("LIVE PORTFOLIO URL: ").append(PORTFOLIO_HOST_URL).append("\n");
        sb.append("CANDIDATE PROFILE:\n");
        sb.append("Name: ").append(about.getFullName() != null ? about.getFullName() : "Prince Gupt").append("\n");
        sb.append("Title / Role: ").append(about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer (Java, Spring Boot, MySQL, React)").append("\n");
        sb.append("Bio: ").append(about.getBio() != null ? about.getBio() : "Building clean, scalable backend systems and high-performance web applications.").append("\n");
        sb.append("Email: ").append(about.getEmail() != null ? about.getEmail() : "princegupt3052@gmail.com").append("\n");
        if (about.getPhone() != null && !about.getPhone().isBlank()) sb.append("Phone: ").append(about.getPhone()).append("\n");

        String hiringLoc = (about.getHiringLocationDetails() != null && !about.getHiringLocationDetails().isBlank())
                ? about.getHiringLocationDetails().trim()
                : ((about.getLocation() != null ? about.getLocation().trim() : "India") + " (Open to Remote, Hybrid, or On-site Relocation)");
        String hiringStatus = (about.getAvailabilityText() != null && !about.getAvailabilityText().isBlank())
                ? about.getAvailabilityText().trim() : "Ready for Immediate Joining (0 days notice)";
        String hiringRoles = (about.getHiringRoles() != null && !about.getHiringRoles().isBlank())
                ? about.getHiringRoles().trim() : "SDE-1 / Software Engineer / Java Backend / Full Stack Developer";
        String hiringNotice = (about.getHiringNoticePeriod() != null && !about.getHiringNoticePeriod().isBlank())
                ? about.getHiringNoticePeriod().trim() : "Immediate Joiner (0 days notice)";

        sb.append("Location: ").append(hiringLoc).append("\n");
        sb.append("Availability: ").append(hiringStatus).append("\n");
        sb.append("Target Roles: ").append(hiringRoles).append("\n");
        sb.append("Notice Period: ").append(hiringNotice).append("\n");
        sb.append("GitHub: ").append(about.getGithubUrl() != null ? about.getGithubUrl() : "https://github.com/princegupt1234").append("\n");
        if (about.getLinkedinUrl() != null && !about.getLinkedinUrl().isBlank()) sb.append("LinkedIn: ").append(about.getLinkedinUrl()).append("\n");
        if (about.getLeetcodeUrl() != null && !about.getLeetcodeUrl().isBlank()) sb.append("LeetCode URL: ").append(about.getLeetcodeUrl()).append("\n");

        // Live LeetCode stats
        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats lcStats =
                leetCodeRepositoryStatsService.fetchStats(about.getGithubUsername());
        String lcUser = (about.getGithubUsername() != null && !about.getGithubUsername().isBlank())
                ? about.getGithubUsername().trim() : "princegupt1234";
        if (lcStats != null && lcStats.available() && lcStats.solved() > 0) {
            sb.append("LeetCode & Problem Solving: ").append(lcStats.solved())
              .append(" problems solved on LeetCode (")
              .append(lcStats.easy()).append(" Easy, ")
              .append(lcStats.medium()).append(" Medium, ")
              .append(lcStats.hard()).append(" Hard). Live profile: https://leetcode.com/u/").append(lcUser).append("/\n");
        } else {
            sb.append("LeetCode & Problem Solving: Actively solving Data Structures & Algorithms problems on LeetCode. Live profile: https://leetcode.com/u/").append(lcUser).append("/\n");
        }
        sb.append("Core DSA Focus: Arrays, Two Pointers, Sliding Window, Trees, Graphs, HashMaps, and Dynamic Programming.\n");

        // Top Skills
        sb.append("\nTOP TECHNICAL SKILLS:\n");
        for (Skill s : skills) {
            sb.append("- ").append(s.getName()).append(" (").append(s.getCategory()).append("): ").append(s.getProficiency()).append("%\n");
        }

        // Projects
        sb.append("\nPORTFOLIO PROJECTS (Showcased on ").append(PORTFOLIO_HOST_URL).append("):\n");
        if (projects.isEmpty()) {
            sb.append("(No published projects currently in the catalog)\n");
        } else {
            for (Project p : projects) {
                sb.append("- ").append(p.getTitle());
                if (Boolean.TRUE.equals(p.getFeatured())) sb.append(" [Featured]");
                sb.append(" [Tech: ").append(p.getTechStack() != null ? p.getTechStack() : "").append("]: ")
                  .append(p.getDescription() != null ? p.getDescription() : "");
                if (p.getEngineeringHighlight() != null && !p.getEngineeringHighlight().isBlank()) {
                    sb.append(" | Highlight: ").append(p.getEngineeringHighlight());
                }
                if (p.getLiveUrl() != null && !p.getLiveUrl().isBlank()) {
                    sb.append(" | Live Demo: ").append(p.getLiveUrl());
                }
                if (p.getGithubUrl() != null && !p.getGithubUrl().isBlank()) {
                    sb.append(" | GitHub: ").append(p.getGithubUrl());
                }
                sb.append("\n");
            }
        }

        // Currently Building
        if (!buildingProjects.isEmpty()) {
            sb.append("\nCURRENTLY BUILDING:\n");
            for (BuildingProject bp : buildingProjects) {
                sb.append("- ").append(bp.getTitle()).append(" [Tech: ").append(bp.getTechStack()).append("]: ")
                  .append(bp.getSummary() != null ? bp.getSummary() : (bp.getDescription() != null ? bp.getDescription() : "")).append("\n");
            }
        }

        // Currently Learning
        if (!learningProjects.isEmpty()) {
            sb.append("\nACTIVE LEARNING & UPSKILLING:\n");
            for (LearningProject lp : learningProjects) {
                sb.append("- ").append(lp.getTitle());
                if (lp.getStatus() != null && !lp.getStatus().isBlank()) sb.append(" (").append(lp.getStatus()).append(")");
                if (lp.getSummary() != null && !lp.getSummary().isBlank()) sb.append(": ").append(lp.getSummary());
                if (lp.getLearningPath() != null && !lp.getLearningPath().isBlank()) sb.append(" | Path: ").append(lp.getLearningPath());
                sb.append("\n");
            }
        }

        // Services Offered
        if (!services.isEmpty()) {
            sb.append("\nSERVICES & EXPERTISE OFFERED:\n");
            for (ServiceItem sv : services) {
                sb.append("- ").append(sv.getTitle()).append(": ").append(sv.getDescription() != null ? sv.getDescription() : "").append("\n");
            }
        }

        // Testimonials / Reviews
        if (!testimonials.isEmpty()) {
            sb.append("\nCLIENT & COLLEAGUE TESTIMONIALS:\n");
            for (Testimonial t : testimonials) {
                sb.append("- \"").append(t.getComment() != null ? t.getComment() : "").append("\" — ").append(t.getName());
                if (t.getRole() != null && !t.getRole().isBlank()) sb.append(" (").append(t.getRole()).append(")");
                if (t.getRating() != null) sb.append(" [Rating: ").append(t.getRating()).append("/5 ⭐]");
                sb.append("\n");
            }
        }

        // Certifications
        if (!certificates.isEmpty()) {
            sb.append("\nCERTIFICATIONS & ACHIEVEMENTS:\n");
            for (Certificate c : certificates) {
                sb.append("- ").append(c.getTitle());
                if (c.getOrganization() != null && !c.getOrganization().isBlank()) sb.append(" (").append(c.getOrganization()).append(")");
                if (c.getIssueDate() != null) sb.append(" - ").append(c.getIssueDate());
                sb.append("\n");
            }
        }

        // Education
        if (!education.isEmpty()) {
            sb.append("\nEDUCATION:\n");
            for (EducationEntry edu : education) {
                sb.append("- ").append(edu.getDegree()).append(" from ").append(edu.getInstitution());
                if (edu.getDuration() != null && !edu.getDuration().isBlank()) sb.append(" (").append(edu.getDuration()).append(")");
                sb.append("\n");
            }
        } else {
            sb.append("\nEDUCATION:\n- B.Tech in Computer Science & Engineering from BIET Lucknow (AKTU)\n");
        }

        // Experience
        if (!experiences.isEmpty()) {
            sb.append("\nWORK EXPERIENCE:\n");
            for (Experience e : experiences) {
                sb.append("- ").append(e.getRole()).append(" at ").append(e.getCompany()).append(" (").append(e.getDuration()).append("): ").append(e.getDescription()).append("\n");
            }
        }

        // Site Stats & Metrics
        if (!siteStats.isEmpty()) {
            sb.append("\nPORTFOLIO METRICS & STATS:\n");
            long totalViews = siteStats.stream().mapToLong(s -> s.getPortfolioViews() != null ? s.getPortfolioViews() : 0L).sum();
            long totalDownloads = siteStats.stream().mapToLong(s -> s.getResumeDownloads() != null ? s.getResumeDownloads() : 0L).sum();
            sb.append("- Total Portfolio Views Recorded: ").append(totalViews).append("\n");
            sb.append("- Total Resume Downloads: ").append(totalDownloads).append("\n");
        }

        // Admin-Trained Knowledge & Corrections
        if (!activeTraining.isEmpty()) {
            sb.append("\n=== VERIFIED ADMIN TRAINING & CORRECTIONS (STRICT GROUND TRUTH) ===\n");
            sb.append("When answering questions relating to these topics, strictly deliver these verified facts:\n");
            for (AiTraining tr : activeTraining) {
                sb.append("• TOPIC/QUESTION: ").append(tr.getQuestionTrigger()).append("\n");
                sb.append("  VERIFIED ANSWER: ").append(tr.getCorrectAnswer()).append("\n");
            }
            sb.append("===================================================================\n");
        }

        return sb.toString();
    }

    public String buildLocalFallbackResponse(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<Project> allVisible = projectRepository.findByVisibleTrueOrderBySortOrderAsc();

        // 1. Check direct active admin training rules
        AiTraining trained = findTrainedOverride(query);
        if (trained != null && trained.getCorrectAnswer() != null && !trained.getCorrectAnswer().isBlank()) {
            return trained.getCorrectAnswer().trim();
        }

        // 2. Check for specific project query by title from database
        for (Project p : allVisible) {
            if (p.getTitle() != null && !p.getTitle().isBlank()) {
                String titleLower = p.getTitle().toLowerCase(Locale.ROOT);
                if (q.contains(titleLower) || (titleLower.length() >= 5 && q.matches(".*\\b" + Pattern.quote(titleLower) + "\\b.*"))) {
                    StringBuilder sb = new StringBuilder("🚀 **" + p.getTitle() + "**");
                    if (Boolean.TRUE.equals(p.getFeatured())) {
                        sb.append(" ⭐ _(Featured Project)_");
                    }
                    sb.append("\n\n");
                    if (p.getDescription() != null && !p.getDescription().isBlank()) {
                        sb.append(p.getDescription()).append("\n\n");
                    }
                    if (p.getTechStack() != null && !p.getTechStack().isBlank()) {
                        sb.append("• **Tech Stack:** ").append(p.getTechStack()).append("\n");
                    }
                    if (p.getEngineeringHighlight() != null && !p.getEngineeringHighlight().isBlank()) {
                        sb.append("• **Highlight:** ").append(p.getEngineeringHighlight()).append("\n");
                    }
                    if (p.getStatus() != null && !p.getStatus().isBlank()) {
                        sb.append("• **Status:** ").append(p.getStatus()).append("\n");
                    }
                    if (p.getLiveUrl() != null && !p.getLiveUrl().isBlank()) {
                        sb.append("• **Live Demo:** [Open Application](").append(p.getLiveUrl()).append(")\n");
                    }
                    if (p.getGithubUrl() != null && !p.getGithubUrl().isBlank()) {
                        sb.append("• **GitHub:** [View Repository](").append(p.getGithubUrl()).append(")\n");
                    }
                    return sb.toString();
                }
            }
        }

        // Greetings
        if (q.matches(".*\\b(hi|hello|hey|greetings|hola|namaste|who are you|intro)\\b.*")) {
            return "👋 **Hello!** I'm Prince's official AI Portfolio Assistant for [" + PORTFOLIO_HOST_URL + "](" + PORTFOLIO_HOST_URL + ").\n\n"
                    + "Prince is a **" + (about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer") + "** specializing in **Java, Spring Boot, MySQL, and React**.\n\n"
                    + "You can ask me about his **backend skills**, **top projects**, **services offered**, **LeetCode DSA record**, or **hiring availability**!";
        }

        // Services & Consulting
        if (q.matches(".*\\b(service|services|offer|consulting|freelance|hire for|build for me|contract)\\b.*")) {
            List<ServiceItem> services = serviceItemRepository.findAllByOrderBySortOrderAsc();
            if (!services.isEmpty()) {
                StringBuilder sb = new StringBuilder("💼 **Services Prince Offers:**\n\n");
                for (ServiceItem s : services) {
                    sb.append("• **").append(s.getTitle()).append(":** ").append(s.getDescription()).append("\n");
                }
                sb.append("\nNeed custom software development or backend architecture? Feel free to reach out directly via email!");
                return sb.toString();
            }
            return "💼 **Engineering Services:**\n\n"
                    + "• **Backend Development:** Scalable Spring Boot microservices, REST APIs, and authentication.\n"
                    + "• **Database Architecture:** MySQL schema design, indexing, and high-performance SQL optimization.\n"
                    + "• **Full Stack Web Apps:** Responsive single-page applications with React and Spring Boot.";
        }

        // Testimonials / Reviews
        if (q.matches(".*\\b(review|reviews|testimonial|testimonials|feedback|recommendation|endorsement)\\b.*")) {
            List<Testimonial> tests = testimonialRepository.findByPublishedTrue();
            if (!tests.isEmpty()) {
                StringBuilder sb = new StringBuilder("⭐ **Client & Peer Endorsements:**\n\n");
                for (Testimonial t : tests) {
                    sb.append("• \"").append(t.getComment() != null ? t.getComment() : "").append("\"\n")
                      .append("  — **").append(t.getName()).append("**");
                    if (t.getRole() != null && !t.getRole().isBlank()) sb.append(" (").append(t.getRole()).append(")");
                    if (t.getRating() != null) sb.append(" [").append(t.getRating()).append("/5 ⭐]");
                    sb.append("\n\n");
                }
                return sb.toString().trim();
            }
            return "⭐ Prince maintains strong engineering endorsements for clean code, reliable delivery, and deep Java/Spring Boot knowledge.";
        }

        // Learning / Upskilling / Currently Building
        if (q.matches(".*\\b(learning|upskilling|currently building|building|roadmap|study)\\b.*")) {
            StringBuilder sb = new StringBuilder();
            List<BuildingProject> bp = buildingProjectRepository.findAllByOrderBySortOrderAsc();
            if (!bp.isEmpty()) {
                sb.append("🚧 **Currently Building:**\n\n");
                for (BuildingProject b : bp) {
                    sb.append("• **").append(b.getTitle()).append("**");
                    if (b.getTechStack() != null) sb.append(" [").append(b.getTechStack()).append("]");
                    sb.append(" — ").append(b.getSummary() != null ? b.getSummary() : (b.getDescription() != null ? b.getDescription() : "")).append("\n");
                }
                sb.append("\n");
            }
            List<LearningProject> lp = learningProjectRepository.findAllByOrderBySortOrderAsc();
            if (!lp.isEmpty()) {
                sb.append("📚 **Active Learning & Upskilling:**\n\n");
                for (LearningProject l : lp) {
                    sb.append("• **").append(l.getTitle()).append("**");
                    if (l.getStatus() != null && !l.getStatus().isBlank()) sb.append(" (").append(l.getStatus()).append(")");
                    if (l.getSummary() != null && !l.getSummary().isBlank()) sb.append(" — ").append(l.getSummary());
                    sb.append("\n");
                }
            }
            if (!sb.isEmpty()) return sb.toString().trim();
        }

        // Skills / Tech stack
        if (q.matches(".*\\b(skill|skills|stack|technologies|language|framework|java|spring|boot|react|mysql|backend|database|sql|tools|tech)\\b.*")) {
            List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
            String skillList = skills.stream().map(Skill::getName).limit(12).collect(Collectors.joining(", "));
            return "🛠️ **Core Technical Stack:**\n\n"
                    + "• **Backend:** Java 17+, Spring Boot 3, Spring MVC, Spring Data JPA/Hibernate, Spring Security (JWT & RBAC), RESTful APIs\n"
                    + "• **Database:** MySQL relational design, indexing, transaction management\n"
                    + "• **Frontend:** React, HTML5, CSS3/Modern Glassmorphism, JavaScript (ES6+)\n"
                    + "• **Tools & Systems:** Git, GitHub, Maven, Docker basics, Linux CLI, Postman\n\n"
                    + (skillList.isEmpty() ? "" : "_Verified Skills:_ " + skillList);
        }

        // Projects
        if (q.matches(".*\\b(project|projects|built|portfolio|work|github|app|apps)\\b.*")) {
            if (allVisible.isEmpty()) {
                return "🚀 Prince's project catalog is showcased directly on the homepage and on [GitHub](https://github.com/princegupt1234)!";
            }

            StringBuilder sb = new StringBuilder("🚀 **Portfolio Projects Built by Prince:**\n\n");
            for (Project p : allVisible) {
                sb.append("• **").append(p.getTitle()).append("**");
                if (Boolean.TRUE.equals(p.getFeatured())) {
                    sb.append(" ⭐ _(Featured)_");
                }
                sb.append(" — ").append(p.getDescription() != null ? p.getDescription() : "").append("\n");
                if (p.getTechStack() != null && !p.getTechStack().isBlank()) {
                    sb.append("  _Stack:_ ").append(p.getTechStack()).append("\n");
                }
                if (p.getEngineeringHighlight() != null && !p.getEngineeringHighlight().isBlank()) {
                    sb.append("  _Highlight:_ ").append(p.getEngineeringHighlight()).append("\n");
                }
                sb.append("\n");
            }

            List<BuildingProject> building = buildingProjectRepository.findAllByOrderBySortOrderAsc();
            if (!building.isEmpty()) {
                sb.append("🚧 **Currently Building:**\n");
                for (BuildingProject b : building) {
                    sb.append("• **").append(b.getTitle()).append("** — ")
                      .append(b.getSummary() != null ? b.getSummary() : (b.getDescription() != null ? b.getDescription() : "")).append("\n");
                }
                sb.append("\n");
            }

            sb.append("Explore all projects with interactive demos in the **#projects** section!");
            return sb.toString();
        }

        // Coding / LeetCode / DSA / Problem Solving
        if (q.matches(".*\\b(leetcode|coding|dsa|problem|problems|algorithm|algorithms|geeksforgeeks|gfg|hackerrank|code)\\b.*")) {
            LeetCodeRepositoryStatsService.LeetCodeRepositoryStats lcStats =
                    leetCodeRepositoryStatsService.fetchStats(about.getGithubUsername());
            String username = (about.getGithubUsername() != null && !about.getGithubUsername().isBlank())
                    ? about.getGithubUsername().trim()
                    : "princegupt1234";

            StringBuilder sb = new StringBuilder("🧠 **Problem Solving & LeetCode Record:**\n\n");
            if (lcStats != null && lcStats.available() && lcStats.solved() > 0) {
                sb.append("• **LeetCode Solved:** **").append(lcStats.solved()).append(" problems** (")
                  .append(lcStats.easy()).append(" Easy, ")
                  .append(lcStats.medium()).append(" Medium, ")
                  .append(lcStats.hard()).append(" Hard).\n");
            } else {
                sb.append("• **LeetCode Progress:** Actively solving algorithmic problems on LeetCode.\n");
            }
            sb.append("• **Core DSA Focus:** Arrays, Two Pointers, Sliding Window, Trees, Graphs, HashMaps, and Dynamic Programming.\n")
              .append("• **Engineering Approach:** Applies clean (O(N) / O(log N)) time and space complexity optimizations to backend systems.\n")
              .append("• **Live Profile:** [leetcode.com/u/").append(username).append("](https://leetcode.com/u/").append(username).append("/)\n\n")
              .append("Check the **#coding** section on the portfolio for live synced repository solutions!");
            return sb.toString();
        }

        // Certifications / Achievements
        if (q.matches(".*\\b(certificate|certificates|certification|certifications|achievement|achievements|credential|credentials)\\b.*")) {
            List<Certificate> certs = certificateRepository.findByVisibleTrueOrderBySortOrderAsc();
            if (certs.isEmpty()) {
                return "📜 Prince holds verified certifications in Full Stack Java Development, Data Structures & Algorithms, and Cloud/DevOps. Check the **#certificates** section for details!";
            }
            StringBuilder sb = new StringBuilder("📜 **Certifications & Achievements:**\n\n");
            for (Certificate c : certs) {
                sb.append("• **").append(c.getTitle()).append("**");
                if (c.getOrganization() != null && !c.getOrganization().isBlank()) {
                    sb.append(" — ").append(c.getOrganization());
                }
                if (c.getIssueDate() != null) {
                    sb.append(" (").append(c.getIssueDate()).append(")");
                }
                sb.append("\n");
            }
            sb.append("\nYou can view credential details and verification badges in the **#certificates** section!");
            return sb.toString();
        }

        // Experience / Career / Internship
        if (q.matches(".*\\b(experience|intern|internship|job|career|work experience|company)\\b.*")) {
            List<Experience> exps = experienceRepository.findByVisibleTrueOrderBySortOrderAsc();
            StringBuilder sb = new StringBuilder("💼 **Experience & Background:**\n\n");
            for (Experience e : exps) {
                sb.append("• **").append(e.getRole()).append("** at ").append(e.getCompany())
                  .append(" (").append(e.getDuration()).append(")\n")
                  .append("  ").append(e.getDescription() != null ? e.getDescription() : "").append("\n\n");
            }
            sb.append("Targeting SDE-1 / Software Engineer roles where high-impact Java/Spring Boot code is needed.");
            return sb.toString();
        }

        // Education / College
        if (q.matches(".*\\b(education|college|degree|aktu|biet|university|study|graduat)\\b.*")) {
            List<EducationEntry> edu = educationEntryRepository.findAllByOrderBySortOrderAsc();
            StringBuilder sb = new StringBuilder("🎓 **Education:**\n\n");
            for (EducationEntry e : edu) {
                sb.append("• **").append(e.getDegree()).append("** — ").append(e.getInstitution())
                  .append(" (").append(e.getDuration()).append(")\n");
            }
            if (edu.isEmpty()) {
                sb.append("• **B.Tech in Computer Science & Engineering** — BIET Lucknow (AKTU)\n");
            }
            return sb.toString();
        }

        // Hiring / Availability / Salary / Notice
        if (q.matches(".*\\b(hire|hiring|available|availability|notice|immediate|joining|remote|relocate|full-time|role|salary|ctc|package|compensation)\\b.*")) {
            String status = (about.getAvailabilityText() != null && !about.getAvailabilityText().isBlank())
                    ? about.getAvailabilityText().trim() : "Open to opportunities";
            String roles = (about.getHiringRoles() != null && !about.getHiringRoles().isBlank())
                    ? about.getHiringRoles().trim()
                    : (about.getRecruiterTargetRole() != null && !about.getRecruiterTargetRole().isBlank()
                        ? about.getRecruiterTargetRole().trim()
                        : "SDE-1 / Software Engineer / Java Backend / Full Stack Developer");
            String notice = (about.getHiringNoticePeriod() != null && !about.getHiringNoticePeriod().isBlank())
                    ? about.getHiringNoticePeriod().trim() : "Immediate Joiner (0 days notice)";
            String loc = (about.getHiringLocationDetails() != null && !about.getHiringLocationDetails().isBlank())
                    ? about.getHiringLocationDetails().trim()
                    : ((about.getLocation() != null ? about.getLocation().trim() : "India") + " (Open to Remote, Hybrid, or On-site Relocation)");
            String contactEmail = (about.getHiringContactEmail() != null && !about.getHiringContactEmail().isBlank())
                    ? about.getHiringContactEmail().trim()
                    : (about.getEmail() != null ? about.getEmail().trim() : "princegupt3052@gmail.com");
            String note = (about.getHiringCustomNote() != null && !about.getHiringCustomNote().isBlank())
                    ? about.getHiringCustomNote().trim()
                    : "For compensation discussions or scheduling an interview, reach out directly at " + contactEmail;

            StringBuilder res = new StringBuilder("🟢 **Hiring Availability:**\n\n");
            res.append("• **Status:** ").append(status).append("\n");
            res.append("• **Target Roles:** ").append(roles).append("\n");
            if (!notice.isBlank()) {
                res.append("• **Notice Period:** ").append(notice).append("\n");
            }
            res.append("• **Location:** ").append(loc).append("\n");
            res.append("• **Contact Email:** [").append(contactEmail).append("](mailto:").append(contactEmail).append(")\n\n");
            res.append(note);
            return res.toString();
        }

        // Contact / Email / Phone
        if (q.matches(".*\\b(contact|email|phone|reach|linkedin|whatsapp|call)\\b.*")) {
            return "📬 **Get In Touch with Prince:**\n\n"
                    + "• **Email:** [" + about.getEmail() + "](mailto:" + about.getEmail() + ")\n"
                    + (about.getPhone() != null ? "• **Phone:** " + about.getPhone() + "\n" : "")
                    + (about.getLinkedinUrl() != null ? "• **LinkedIn:** [View Profile](" + about.getLinkedinUrl() + ")\n" : "")
                    + (about.getGithubUrl() != null ? "• **GitHub:** [github.com/princegupt1234](" + about.getGithubUrl() + ")\n" : "")
                    + "\nYou can also send a direct message using the contact form at the bottom of the page!";
        }

        // Resume
        if (q.matches(".*\\b(resume|cv|pdf|download)\\b.*")) {
            return "📄 **Prince's Resume:**\n\n"
                    + "• You can preview the resume in your browser using the **'Resume'** button in the navbar.\n"
                    + "• Download the PDF directly here: [Download Resume PDF](/resume/download)";
        }

        // Guestbook
        if (q.matches(".*\\b(guestbook|sign|endorse|message board)\\b.*")) {
            return "📖 **Community Guestbook:**\n\n"
                    + "You can leave a note, feedback, or endorsement in the **#guestbook** section on the homepage! Visitors and recruiters can leave their thoughts there.";
        }

        // Questions / Help / Suggestions
        if (q.matches(".*\\b(question|questions|help|suggest|suggestion|suggestions|what can you do|what to ask|options|menu|commands)\\b.*")) {
            return "💡 **Here are key questions you can ask me:**\n\n"
                    + "• **Backend Skills:** _\"What are Prince's core backend and Spring Boot skills?\"_\n"
                    + "• **Featured Projects:** _\"Tell me about Prince's top projects and tech stacks\"_\n"
                    + "• **Hiring & Availability:** _\"Can Prince join immediately or relocate?\"_\n"
                    + "• **LeetCode & DSA:** _\"What is Prince's DSA problem-solving track record?\"_\n"
                    + "• **Direct Contact:** _\"How can I schedule an interview or reach Prince?\"_\n"
                    + "• **Resume:** _\"Where can I download or preview Prince's resume?\"_\n\n"
                    + "You can also tap any of the quick suggestion chips above!";
        }

        // Default courteous summary
        String shortBio = (about.getBio() != null && !about.getBio().isBlank())
                ? (about.getBio().length() > 160 ? about.getBio().substring(0, 160).trim() + "..." : about.getBio().trim())
                : "Specialized in high-throughput Spring Boot microservices, MySQL architecture, and modern responsive frontends.";

        return "✨ **Prince Gupt** — **" + (about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer") + "**\n\n"
                + shortBio + "\n\n"
                + "💡 **Try asking:**\n"
                + "• _\"What are your core backend skills?\"_\n"
                + "• _\"Tell me about your top projects\"_\n"
                + "• _\"What is your hiring availability?\"_\n"
                + "• _\"How do I contact you?\"_";
    }
}
