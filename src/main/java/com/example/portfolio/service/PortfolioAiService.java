package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PortfolioAiService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioAiService.class);

    private final AboutInfoRepository aboutInfoRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final RestClient restClient;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    public PortfolioAiService(AboutInfoRepository aboutInfoRepository,
                              SkillRepository skillRepository,
                              ProjectRepository projectRepository,
                              ExperienceRepository experienceRepository,
                              EducationEntryRepository educationEntryRepository,
                              BuildingProjectRepository buildingProjectRepository) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.restClient = RestClient.builder().build();
    }

    public String answer(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "Please feel free to ask any question about Prince's skills, projects, experience, or hiring details!";
        }

        String query = userQuery.trim();

        // Check if Gemini API key is configured
        String effectiveKey = geminiApiKey;
        if (effectiveKey == null || effectiveKey.trim().isEmpty()) {
            effectiveKey = System.getenv("GEMINI_API_KEY");
        }

        if (effectiveKey != null && !effectiveKey.trim().isEmpty()) {
            try {
                String aiResponse = callGeminiApi(query, effectiveKey.trim());
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    return aiResponse;
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed or timed out (falling back to local knowledge engine): {}", ex.getMessage());
            }
        }

        // Fallback intelligent domain engine using live database context
        return buildLocalFallbackResponse(query);
    }

    private String callGeminiApi(String userQuery, String apiKey) throws Exception {
        String systemContext = buildPortfolioContext();
        String prompt = "You are the AI portfolio assistant for Prince Gupt, an enthusiastic Full Stack Software Engineer (Java, Spring Boot, MySQL, React). "
                + "Answer the visitor's question professionally, concisely (under 120 words), and engagingly. "
                + "Always ground your facts strictly on the candidate profile data provided below:\n\n"
                + systemContext + "\n\n"
                + "Visitor Question: " + userQuery + "\n"
                + "Answer in clear, courteous markdown (bullet points if helpful):";

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentObj = Map.of("role", "user", "parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(contentObj));

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + apiKey;

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
        return null;
    }

    private String buildPortfolioContext() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
        List<Project> projects = projectRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<Experience> experiences = experienceRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<BuildingProject> buildingProjects = buildingProjectRepository.findAllByOrderBySortOrderAsc();

        StringBuilder sb = new StringBuilder();
        sb.append("CANDIDATE INFO:\n");
        sb.append("Name: ").append(about.getFullName()).append("\n");
        sb.append("Title: ").append(about.getTitle()).append("\n");
        sb.append("Bio: ").append(about.getBio()).append("\n");
        sb.append("Email: ").append(about.getEmail()).append("\n");
        sb.append("Phone: ").append(about.getPhone()).append("\n");
        sb.append("Location: ").append(about.getLocation()).append("\n");
        sb.append("Availability: ").append(about.getAvailabilityText()).append("\n");
        sb.append("LeetCode / Problem Solving: 350+ DSA problems solved across LeetCode and GeeksforGeeks.\n");

        sb.append("\nTOP SKILLS:\n");
        for (Skill s : skills) {
            sb.append("- ").append(s.getName()).append(" (").append(s.getCategory()).append("): ").append(s.getProficiency()).append("%\n");
        }

        sb.append("\nPORTFOLIO PROJECTS (from admin panel / frontend visible):\n");
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
                    sb.append(" | Live: ").append(p.getLiveUrl());
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

        sb.append("\nEXPERIENCE:\n");
        for (Experience e : experiences) {
            sb.append("- ").append(e.getRole()).append(" at ").append(e.getCompany()).append(" (").append(e.getDuration()).append("): ").append(e.getDescription()).append("\n");
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
            return "👋 **Hello!** I'm Prince's AI Portfolio Assistant.\n\n"
                    + "Prince is a **" + (about.getTitle() != null ? about.getTitle() : "Full Stack Software Engineer") + "** specializing in **Java, Spring Boot, MySQL, and React**.\n\n"
                    + "You can ask me about his **backend skills**, **top projects**, **LeetCode DSA record**, or **hiring availability**!";
        }

        // Skills / Tech stack
        if (q.matches(".*\\b(skill|skills|stack|technologies|language|framework|java|spring|boot|react|mysql|backend)\\b.*")) {
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

        // Coding / LeetCode / DSA
        if (q.matches(".*\\b(leetcode|coding|dsa|problem|algorithm|geeksforgeeks|gfg|hackerrank)\\b.*")) {
            return "🧠 **Problem Solving & DSA Record:**\n\n"
                    + "• Solved **350+ algorithmic problems** across LeetCode and GeeksforGeeks.\n"
                    + "• Strong command over **Arrays, Two Pointers, Sliding Window, Trees, Graphs, HashMaps, and Dynamic Programming**.\n"
                    + "• Consistently applies clean (O(N)) time and space complexity optimizations to backend systems.\n\n"
                    + "Check the **#coding** section for live synced repository solutions!";
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
                + "Try asking:\n"
                + "• _\"What are your core backend skills?\"_\n"
                + "• _\"Tell me about your top projects\"_\n"
                + "• _\"What is your LeetCode and DSA experience?\"_\n"
                + "• _\"How can I interview or hire you?\"_";
    }
}
