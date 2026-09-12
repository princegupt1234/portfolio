package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SkillRepository;
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
    private final CertificateRepository certificateRepository;
    private final LeetCodeRepositoryStatsService leetCodeRepositoryStatsService;
    private final RestClient restClient;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    public PortfolioAiService(AboutInfoRepository aboutInfoRepository,
                              SkillRepository skillRepository,
                              ProjectRepository projectRepository,
                              ExperienceRepository experienceRepository,
                              EducationEntryRepository educationEntryRepository,
                              BuildingProjectRepository buildingProjectRepository,
                              CertificateRepository certificateRepository,
                              LeetCodeRepositoryStatsService leetCodeRepositoryStatsService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.certificateRepository = certificateRepository;
        this.leetCodeRepositoryStatsService = leetCodeRepositoryStatsService;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(6));
        requestFactory.setReadTimeout(Duration.ofSeconds(12));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public String answer(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "Please feel free to ask any question about Prince's skills, projects, experience, or hiring details!";
        }

        String query = userQuery.trim();

        // Check configured Gemini API key (supports GEMINI_API_KEY, GEMINI_API, GIMINI_API, etc.)
        String effectiveKey = resolveGeminiApiKey();

        if (effectiveKey != null && !effectiveKey.trim().isEmpty()) {
            try {
                String aiResponse = callGeminiApi(query, effectiveKey.trim());
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    return aiResponse;
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed (falling back to local portfolio engine): {}", ex.getMessage());
            }
        }

        // Fallback intelligent domain engine using live database context
        return buildLocalFallbackResponse(query);
    }

    private String resolveGeminiApiKey() {
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty()) {
            return geminiApiKey.trim();
        }
        for (String envName : List.of("GEMINI_API_KEY", "GEMINI_API", "GIMINI_API", "GIMINI_API_KEY", "GEMINI_KEY", "GOOGLE_API_KEY")) {
            String val = System.getenv(envName);
            if (val != null && !val.trim().isEmpty()) {
                return val.trim();
            }
        }
        return null;
    }

    private String callGeminiApi(String userQuery, String apiKey) {
        String systemContext = buildPortfolioContext();
        String prompt = "You are the official interactive AI portfolio assistant for Prince Gupt, hosted on his live portfolio website: " + PORTFOLIO_HOST_URL + ".\n\n"
                + "MISSION:\n"
                + "Represent Prince accurately, politely, and professionally to recruiters, engineering managers, and visitors.\n\n"
                + "CRITICAL RULES:\n"
                + "1. STRICT TRUTH: Answer all questions based strictly on the verified portfolio data provided below. Do NOT hallucinate or exaggerate skills, company names, projects, or degrees.\n"
                + "2. LEETCODE & DSA: State Prince's actual solved count and focus areas from the data. Never claim fabricated numbers or 350+.\n"
                + "3. PROJECTS: Talk about his real projects (such as POS Billing System, Travel Booking Website, Personal Portfolio) with their real tech stacks (Spring Boot, MySQL, React, etc.) and refer to the live demos.\n"
                + "4. HIRING & AVAILABILITY: State his immediate joining availability (0 days notice) and provide his contact email.\n"
                + "5. FORMATTING: Use crisp markdown with bullet points where appropriate. Keep responses engaging, courteous, and concise (under 130 words).\n\n"
                + "=== PRINCE GUPT PORTFOLIO PROFILE (HOSTED ON " + PORTFOLIO_HOST_URL + ") ===\n"
                + systemContext + "\n"
                + "=== END OF PORTFOLIO PROFILE ===\n\n"
                + "Visitor Question: " + userQuery + "\n\n"
                + "Answer:";

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentObj = Map.of("role", "user", "parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(contentObj));

        List<String> models = List.of(
                (geminiModel != null && !geminiModel.isBlank()) ? geminiModel.trim() : "gemini-2.0-flash",
                "gemini-2.0-flash",
                "gemini-1.5-flash"
        ).stream().distinct().toList();

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
                            Object firstPart = parts.get(0);
                            if (firstPart instanceof Map<?, ?> partMap && partMap.get("text") instanceof String text) {
                                return text;
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

    private String buildPortfolioContext() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
        List<Project> projects = projectRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<Experience> experiences = experienceRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<EducationEntry> education = educationEntryRepository.findAllByOrderBySortOrderAsc();
        List<BuildingProject> buildingProjects = buildingProjectRepository.findAllByOrderBySortOrderAsc();
        List<Certificate> certificates = certificateRepository.findByVisibleTrueOrderBySortOrderAsc();

        StringBuilder sb = new StringBuilder();
        sb.append("LIVE PORTFOLIO URL: ").append(PORTFOLIO_HOST_URL).append("\n");
        sb.append("CANDIDATE INFO:\n");
        sb.append("Name: ").append(about.getFullName() != null ? about.getFullName() : "Prince Gupt").append("\n");
        sb.append("Title / Role: ").append(about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer (Java, Spring Boot, MySQL, React)").append("\n");
        sb.append("Bio: ").append(about.getBio() != null ? about.getBio() : "Building clean, scalable backend systems and high-performance web applications.").append("\n");
        sb.append("Email: ").append(about.getEmail() != null ? about.getEmail() : "princegupt3052@gmail.com").append("\n");
        if (about.getPhone() != null && !about.getPhone().isBlank()) sb.append("Phone: ").append(about.getPhone()).append("\n");
        sb.append("Location: ").append(about.getLocation() != null ? about.getLocation() : "India").append(" (Open to Remote, Hybrid, or On-site Relocation)\n");
        sb.append("Availability: ").append(about.getAvailabilityText() != null ? about.getAvailabilityText() : "Ready for Immediate Joining (0 days notice)").append("\n");
        sb.append("GitHub: ").append(about.getGithubUrl() != null ? about.getGithubUrl() : "https://github.com/princegupt1234").append("\n");
        if (about.getLinkedinUrl() != null && !about.getLinkedinUrl().isBlank()) sb.append("LinkedIn: ").append(about.getLinkedinUrl()).append("\n");

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

        sb.append("\nTOP SKILLS:\n");
        for (Skill s : skills) {
            sb.append("- ").append(s.getName()).append(" (").append(s.getCategory()).append("): ").append(s.getProficiency()).append("%\n");
        }

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

        if (!buildingProjects.isEmpty()) {
            sb.append("\nCURRENTLY BUILDING:\n");
            for (BuildingProject bp : buildingProjects) {
                sb.append("- ").append(bp.getTitle()).append(" [Tech: ").append(bp.getTechStack()).append("]: ")
                  .append(bp.getSummary() != null ? bp.getSummary() : (bp.getDescription() != null ? bp.getDescription() : "")).append("\n");
            }
        }

        if (!certificates.isEmpty()) {
            sb.append("\nCERTIFICATIONS & ACHIEVEMENTS:\n");
            for (Certificate c : certificates) {
                sb.append("- ").append(c.getTitle());
                if (c.getOrganization() != null && !c.getOrganization().isBlank()) sb.append(" (").append(c.getOrganization()).append(")");
                if (c.getIssueDate() != null) sb.append(" - ").append(c.getIssueDate());
                sb.append("\n");
            }
        }

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

        if (!experiences.isEmpty()) {
            sb.append("\nWORK EXPERIENCE:\n");
            for (Experience e : experiences) {
                sb.append("- ").append(e.getRole()).append(" at ").append(e.getCompany()).append(" (").append(e.getDuration()).append("): ").append(e.getDescription()).append("\n");
            }
        }

        return sb.toString();
    }

    public String buildLocalFallbackResponse(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<Project> allVisible = projectRepository.findByVisibleTrueOrderBySortOrderAsc();

        // 1. Check for specific project query by title from admin panel / database
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
        if (q.matches(".*\\b(hi|hello|hey|greetings|hola|namaste|who are you)\\b.*")) {
            return "👋 **Hello!** I'm Prince's AI Portfolio Assistant for [" + PORTFOLIO_HOST_URL + "](" + PORTFOLIO_HOST_URL + ").\n\n"
                    + "Prince is a **" + (about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer") + "** specializing in **Java, Spring Boot, MySQL, and React**.\n\n"
                    + "You can ask me about his **backend skills**, **top projects**, **LeetCode DSA record**, or **hiring availability**!";
        }

        // Skills / Tech stack
        if (q.matches(".*\\b(skill|skills|stack|technologies|language|framework|java|spring|boot|react|mysql|backend|database|sql|tools|tech)\\b.*")) {
            List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
            String skillList = skills.stream().map(Skill::getName).limit(10).collect(Collectors.joining(", "));
            return "🛠️ **Core Technical Stack:**\n\n"
                    + "• **Backend:** Java 17+, Spring Boot 3, Spring MVC, Spring Data JPA/Hibernate, Spring Security (JWT & RBAC), RESTful APIs\n"
                    + "• **Database:** MySQL relational design, indexing, transaction management\n"
                    + "• **Frontend:** React, HTML5, CSS3/Modern Glassmorphism, JavaScript (ES6+)\n"
                    + "• **Tools & Systems:** Git, GitHub, Maven, Docker basics, Linux CLI, Postman\n\n"
                    + (skillList.isEmpty() ? "" : "_Verified Skills:_ " + skillList);
        }

        // Projects (all visible projects from Admin Panel matching portfolio frontend)
        if (q.matches(".*\\b(project|projects|built|portfolio|work|github|app|apps)\\b.*")) {
            if (allVisible.isEmpty()) {
                return "🚀 Prince's project catalog is currently being updated in the admin panel. "
                        + "You can check back shortly or explore his repositories directly on [GitHub](https://github.com/princegupt1234)!";
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
                for (BuildingProject bp : building) {
                    sb.append("• **").append(bp.getTitle()).append("**");
                    if (bp.getStatus() != null && !bp.getStatus().isBlank()) {
                        sb.append(" (").append(bp.getStatus()).append(")");
                    }
                    sb.append(" — ").append(bp.getSummary() != null ? bp.getSummary() : (bp.getDescription() != null ? bp.getDescription() : "")).append("\n");
                    if (bp.getTechStack() != null && !bp.getTechStack().isBlank()) {
                        sb.append("  _Stack:_ ").append(bp.getTechStack().replace("|", ", ")).append("\n");
                    }
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
            sb.append("Targeting SDE-1 / Junior Backend Engineer roles where high-impact Java/Spring Boot code is needed.");
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

        // Hiring / Availability
        if (q.matches(".*\\b(hire|hiring|available|availability|notice|immediate|joining|remote|relocate|full-time|role|salary)\\b.*")) {
            return "🟢 **Hiring Availability:**\n\n"
                    + "• **Status:** " + (about.getAvailabilityText() != null ? about.getAvailabilityText() : "Ready for Immediate Joining (0 days notice)") + "\n"
                    + "• **Roles:** SDE-1 / Software Engineer / Java Backend / Full Stack Developer\n"
                    + "• **Location:** " + (about.getLocation() != null ? about.getLocation() : "India") + " (Open to Remote, Hybrid, or On-site Relocation)\n"
                    + "• **Contact:** [" + about.getEmail() + "](mailto:" + about.getEmail() + ")\n\n"
                    + "Click the **30s Recruiter Brief** button in the header for the executive candidate summary!";
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
                    + "• You can preview the resume in your browser using the **'View'** button in the navbar.\n"
                    + "• Download the PDF directly here: [Download Resume PDF](/resume/download)";
        }

        // Default courteous summary
        return "✨ **Prince Gupt** is a **" + (about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer") + "**.\n\n"
                + (about.getBio() != null ? about.getBio() : "Specialized in high-throughput Spring Boot REST microservices, clean MySQL schema design, and modern responsive frontends.") + "\n\n"
                + "Live Portfolio: [" + PORTFOLIO_HOST_URL + "](" + PORTFOLIO_HOST_URL + ")\n\n"
                + "Try asking:\n"
                + "• _\"What are your core backend skills?\"_\n"
                + "• _\"Tell me about your top projects\"_\n"
                + "• _\"What is your LeetCode and DSA experience?\"_\n"
                + "• _\"How can I interview or hire you?\"_";
    }
}
