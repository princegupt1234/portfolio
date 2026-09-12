package com.example.portfolio.config;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.Admin;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import com.example.portfolio.service.GithubStatsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the database with Prince Gupt's real resume content and a default
 * admin account on first boot, ONLY if the tables are empty. Everything here
 * is fully editable afterwards from the admin panel.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final AboutInfoRepository aboutInfoRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final TestimonialRepository testimonialRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final GithubStatsService githubStatsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-username}")
    private String defaultAdminUsername;
    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;
    @Value("${app.admin.default-email}")
    private String defaultAdminEmail;

    public DataInitializer(AdminRepository adminRepository, AboutInfoRepository aboutInfoRepository,
                            EducationEntryRepository educationEntryRepository, SkillRepository skillRepository,
                            ExperienceRepository experienceRepository, ProjectRepository projectRepository,
                            CertificateRepository certificateRepository, ServiceItemRepository serviceItemRepository,
                            TestimonialRepository testimonialRepository,
                            BuildingProjectRepository buildingProjectRepository,
                            LearningProjectRepository learningProjectRepository,
                            GithubStatsService githubStatsService, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.githubStatsService = githubStatsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedAbout();
        seedEducation();
        seedBuildingProjects();
        seedLearningProjects();
        seedSkills();
        seedExperience();
        seedProjects();
        seedCertificates();
        seedServices();
        seedTestimonials();
        // Pre-warm external API caches asynchronously so first page load is fast
        aboutInfoRepository.findAll().stream().findFirst().ifPresent(about -> {
            if (about.getGithubUsername() != null) githubStatsService.fetchStats(about.getGithubUsername());
        });
    }

    private void seedAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername(defaultAdminUsername);
            admin.setEmail(defaultAdminEmail);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setRole("ROLE_ADMIN");
            adminRepository.save(admin);
        }
    }

    private void seedAbout() {
        if (aboutInfoRepository.count() == 0) {
            AboutInfo info = new AboutInfo();
            info.setFullName("Prince Gupt");
            info.setTitle("Full Stack Developer | Final Year CSE Student");
            info.setHeroEyebrow("// Hello, I'm");
            info.setHeroPhrases("Full Stack Developer|Java + Spring Boot Enthusiast|React & Next.js Developer|Final Year CSE Student");
            info.setHeroTypingSpeed(75);
            info.setHeroDeletingSpeed(35);
            info.setHeroPauseDuration(1600);
            info.setBio("Final-year B.Tech CSE student at BIET Lucknow (AKTU), currently pursuing Java Full " +
                    "Stack Development training. Comfortable across frontend, backend, and database layers, " +
                    "with a strong, self-driven foundation in data structures and algorithms.");
            info.setCareerObjective("Seeking an entry-level SDE role or final-year internship to contribute to " +
                    "real-world engineering teams, building production-oriented full stack features.");
            info.setPhone("+91-7275807576");
            info.setEmail("princegupt3052@gmail.com");
            info.setLocation("Lucknow, Uttar Pradesh");
            info.setGithubUrl("https://github.com/princegupt1234");
            info.setLinkedinUrl("https://linkedin.com/in/prince-gupt-175289322");
            info.setWhatsappUrl("https://wa.me/917275807576");
            info.setGithubUsername("princegupt1234");
            info.setHeroTechStack("Java|Spring Boot|REST APIs|MySQL|React");
            info.setCurrentlyLearning("Advanced Spring Boot, System Design, DSA problem solving, and exploring React + Next.js for modern frontends.");
            info.setAvailabilityText("Open to opportunities");
            info.setAvailabilityVisible(true);
            info.setQuickStats("10+::Projects Built::fa-solid fa-folder-open|250+::DSA Solved::fa-solid fa-code|1+ Yrs::Hands-on Dev::fa-solid fa-laptop-code|100%::Delivery & Testing::fa-solid fa-circle-check");
            info.setQuickStatsVisible(true);
            info.setWorkPreference("Full-time (Remote / Hybrid / On-site)");
            info.setPreferredLocations("Bengaluru, Delhi NCR, Pune, Remote");
            info.setLanguagesSpoken("English (Professional), Hindi (Native)");
            info.setTerminalEnabled(true);
            info.setFooterTagline("Full Stack Developer");
            info.setFooterSub("Building clean, scalable software with Java & Spring Boot.");
            info.setRecruiterPitchEnabled(true);
            info.setRecruiterTargetRole("Target: Software Engineer / Backend Developer (SDE-1)");
            info.setRecruiterMetrics("B.Tech CSE :: Target: SDE-1 / Backend | 350+ :: DSA & Algorithmic Solved | Production-Ready :: Spring Boot & REST APIs");
            info.setRecruiterHighlights("Core Backend Mastery :: Proficient in Java 17+, Spring Boot 3, Spring Data JPA/Hibernate, Spring Security (JWT & RBAC), and MySQL relational database indexing. :: fa-solid fa-server | Architecture & Clean Code :: Hands-on expertise with MVC architecture, RESTful API design principles, DTO patterns, exception handling, and Maven builds. :: fa-solid fa-layer-group | Full-Stack Versatility :: Modern interactive UI with HTML5/CSS3/JavaScript & React. Strong CLI, Docker, Git workflow, and fast ramp-up capability. :: fa-solid fa-bolt-lightning");
            info.setRecruiterPitchCopyText("Prince Gupt | Full Stack Software Engineer (Java, Spring Boot, MySQL, React). 350+ LeetCode DSA solved. Ready for immediate hire (0-day notice) for SDE-1 / Software Engineer roles. Email: princegupt3052@gmail.com | Portfolio: https://github.com/princegupt1234");
            info.setAiChatEnabled(true);
            info.setAiChatWelcomeMessage("👋 Hello! I am Prince's interactive AI assistant. Ask me anything about Prince's Java & Spring Boot mastery, featured projects, 350+ LeetCode DSA record, or hiring availability!");
            info.setAiChatPromptChips("Core Backend :: What are Prince's core backend skills? | Top Projects :: Tell me about your top projects | LeetCode 350+ :: What is your problem solving and LeetCode record? | Availability :: Are you available for immediate hiring? | Contact Info :: How do I contact Prince or schedule an interview?");
            aboutInfoRepository.save(info);
        } else {
            aboutInfoRepository.findAll().stream().findFirst().ifPresent(info -> {
                boolean changed = false;
                if (info.getQuickStats() == null || info.getQuickStats().isBlank()) {
                    info.setQuickStats("10+::Projects Built::fa-solid fa-folder-open|250+::DSA Solved::fa-solid fa-code|1+ Yrs::Hands-on Dev::fa-solid fa-laptop-code|100%::Delivery & Testing::fa-solid fa-circle-check");
                    info.setQuickStatsVisible(true);
                    changed = true;
                }
                if (info.getWorkPreference() == null || info.getWorkPreference().isBlank()) {
                    info.setWorkPreference("Full-time (Remote / Hybrid / On-site)");
                    changed = true;
                }
                if (info.getPreferredLocations() == null || info.getPreferredLocations().isBlank()) {
                    info.setPreferredLocations("Bengaluru, Delhi NCR, Pune, Remote");
                    changed = true;
                }
                if (info.getLanguagesSpoken() == null || info.getLanguagesSpoken().isBlank()) {
                    info.setLanguagesSpoken("English (Professional), Hindi (Native)");
                    changed = true;
                }
                if (info.getTerminalEnabled() == null) {
                    info.setTerminalEnabled(true);
                    changed = true;
                }
                if (info.getRecruiterPitchEnabled() == null) {
                    info.setRecruiterPitchEnabled(true);
                    changed = true;
                }
                if (info.getRecruiterTargetRole() == null || info.getRecruiterTargetRole().isBlank()) {
                    info.setRecruiterTargetRole("Target: Software Engineer / Backend Developer (SDE-1)");
                    changed = true;
                }
                if (info.getRecruiterMetrics() == null || info.getRecruiterMetrics().isBlank()) {
                    info.setRecruiterMetrics("B.Tech CSE :: Target: SDE-1 / Backend | 350+ :: DSA & Algorithmic Solved | Production-Ready :: Spring Boot & REST APIs");
                    changed = true;
                }
                if (info.getRecruiterHighlights() == null || info.getRecruiterHighlights().isBlank()) {
                    info.setRecruiterHighlights("Core Backend Mastery :: Proficient in Java 17+, Spring Boot 3, Spring Data JPA/Hibernate, Spring Security (JWT & RBAC), and MySQL relational database indexing. :: fa-solid fa-server | Architecture & Clean Code :: Hands-on expertise with MVC architecture, RESTful API design principles, DTO patterns, exception handling, and Maven builds. :: fa-solid fa-layer-group | Full-Stack Versatility :: Modern interactive UI with HTML5/CSS3/JavaScript & React. Strong CLI, Docker, Git workflow, and fast ramp-up capability. :: fa-solid fa-bolt-lightning");
                    changed = true;
                }
                if (info.getRecruiterPitchCopyText() == null || info.getRecruiterPitchCopyText().isBlank()) {
                    info.setRecruiterPitchCopyText("Prince Gupt | Full Stack Software Engineer (Java, Spring Boot, MySQL, React). 350+ LeetCode DSA solved. Ready for immediate hire (0-day notice) for SDE-1 / Software Engineer roles. Email: princegupt3052@gmail.com | Portfolio: https://github.com/princegupt1234");
                    changed = true;
                }
                if (info.getAiChatEnabled() == null) {
                    info.setAiChatEnabled(true);
                    changed = true;
                }
                if (info.getAiChatWelcomeMessage() == null || info.getAiChatWelcomeMessage().isBlank()) {
                    info.setAiChatWelcomeMessage("👋 Hello! I am Prince's interactive AI assistant. Ask me anything about Prince's Java & Spring Boot mastery, featured projects, 350+ LeetCode DSA record, or hiring availability!");
                    changed = true;
                }
                if (info.getAiChatPromptChips() == null || info.getAiChatPromptChips().isBlank()) {
                    info.setAiChatPromptChips("Core Backend :: What are Prince's core backend skills? | Top Projects :: Tell me about your top projects | LeetCode 350+ :: What is your problem solving and LeetCode record? | Availability :: Are you available for immediate hiring? | Contact Info :: How do I contact Prince or schedule an interview?");
                    changed = true;
                }
                if (changed) {
                    aboutInfoRepository.save(info);
                }
            });
        }
    }

    private void seedLearningProjects() {
        if (learningProjectRepository.count() == 0) {
            // Migrate existing currentlyLearning text from AboutInfo if present
            String existingSummary = aboutInfoRepository.findAll().stream().findFirst()
                    .map(a -> a.getCurrentlyLearning()).orElse(null);
            String existingPath = aboutInfoRepository.findAll().stream().findFirst()
                    .map(a -> a.getLearningPath()).orElse(null);
            String existingCards = aboutInfoRepository.findAll().stream().findFirst()
                    .map(a -> a.getLearningCards()).orElse(null);

            LearningProject lp1 = new LearningProject();
            lp1.setTitle("Spring Boot Advanced");
            lp1.setSummary(existingSummary != null ? existingSummary : "Deepening backend expertise with advanced Spring Boot patterns, security, and microservices.");
            lp1.setLearningPath(existingPath != null ? existingPath : "Spring Boot Advanced|Security|Microservices|JPA");
            lp1.setLearningCards(existingCards != null ? existingCards :
                "Spring Boot Advanced::fa-solid fa-leaf::Backend::Security, microservices & JPA|" +
                "DSA Practice::fa-solid fa-code::Algorithms::Daily problem solving|" +
                "System Design::fa-solid fa-sitemap::Architecture::Scalable distributed systems|" +
                "React + Next.js::fa-brands fa-react::Frontend::Modern full-stack web apps|" +
                "Cloud & DevOps::fa-brands fa-aws::DevOps::AWS, Docker & CI/CD pipelines");
            lp1.setStatus("In Progress");
            lp1.setSortOrder(1);
            learningProjectRepository.save(lp1);
        }
    }

    private void seedBuildingProjects() {
        if (buildingProjectRepository.count() == 0) {
            BuildingProject bp = new BuildingProject();
            bp.setTitle("Portfolio Admin System");
            bp.setSummary("Building a full-stack portfolio management system with a Spring Boot backend, admin panel, CRUD operations, and database integration.");
            bp.setDescription("Full-stack portfolio management system with a Spring Boot admin panel for managing profile, skills, projects, experience, education, and other portfolio content.");
            bp.setTechStack("Java|Spring Boot|Spring MVC|MySQL|Thymeleaf|REST API");
            bp.setProjectUrl("https://github.com/princegupt1234");
            bp.setStatus("In Progress");
            bp.setProgress(65);
            bp.setSortOrder(1);
            buildingProjectRepository.save(bp);
        }
    }

    private void seedEducation() {
        if (educationEntryRepository.count() == 0) {
            educationEntryRepository.save(edu("B.Tech, Computer Science & Engineering",
                    "Bansal Institute of Engineering & Technology, Lucknow (AKTU)", "Aug 2023 - Aug 2027", "Pursuing", 1));
            educationEntryRepository.save(edu("Intermediate (12th) - PCM",
                    "Udit Narayan Intermediate College, Padrauna, Kushinagar (UP Board)", "2022", "63%", 2));
            educationEntryRepository.save(edu("High School (10th)",
                    "Shree Chhatathu Prasad Uchchatar Madhyamik Vidyalaya, Kushinagar (UP Board)", "2020", "79%", 3));
        }
    }

    private EducationEntry edu(String degree, String institution, String duration, String score, int order) {
        EducationEntry e = new EducationEntry();
        e.setDegree(degree);
        e.setInstitution(institution);
        e.setDuration(duration);
        e.setScoreLabel(score);
        e.setSortOrder(order);
        return e;
    }

    private void seedSkills() {
        if (skillRepository.count() == 0) {
            int i = 0;
            skillRepository.save(skill("Java", "Programming", "fa-brands fa-java", 85, i++));
            skillRepository.save(skill("C++", "Programming", "fa-solid fa-code", 75, i++));
            skillRepository.save(skill("C", "Programming", "fa-solid fa-code", 70, i++));
            skillRepository.save(skill("Spring Boot", "Backend", "fa-solid fa-leaf", 78, i++));
            skillRepository.save(skill("REST API Design", "Backend", "fa-solid fa-server", 80, i++));
            skillRepository.save(skill("Node.js", "Backend", "fa-brands fa-node-js", 75, i++));
            skillRepository.save(skill("Express.js", "Backend", "fa-solid fa-server", 72, i++));
            skillRepository.save(skill("React.js", "Frontend", "fa-brands fa-react", 80, i++));
            skillRepository.save(skill("Next.js", "Frontend", "fa-solid fa-n", 75, i++));
            skillRepository.save(skill("TypeScript", "Frontend", "fa-solid fa-code", 72, i++));
            skillRepository.save(skill("HTML5", "Frontend", "fa-brands fa-html5", 90, i++));
            skillRepository.save(skill("CSS3", "Frontend", "fa-brands fa-css3-alt", 88, i++));
            skillRepository.save(skill("JavaScript", "Frontend", "fa-brands fa-js", 82, i++));
            skillRepository.save(skill("Bootstrap", "Frontend", "fa-brands fa-bootstrap", 80, i++));
            skillRepository.save(skill("Thymeleaf", "Frontend", "fa-solid fa-fire", 70, i++));
            skillRepository.save(skill("MySQL", "Database", "fa-solid fa-database", 80, i++));
            skillRepository.save(skill("MongoDB", "Database", "fa-solid fa-leaf", 75, i++));
            skillRepository.save(skill("Git", "Tools", "fa-brands fa-git-alt", 85, i++));
            skillRepository.save(skill("GitHub", "Tools", "fa-brands fa-github", 85, i++));
            skillRepository.save(skill("VS Code", "Tools", "fa-solid fa-code", 90, i++));
            skillRepository.save(skill("Postman", "Tools", "fa-solid fa-paper-plane", 78, i++));
        }
    }

    private Skill skill(String name, String category, String icon, int proficiency, int order) {
        Skill s = new Skill();
        s.setName(name);
        s.setCategory(category);
        s.setIcon(icon);
        s.setProficiency(proficiency);
        s.setSortOrder(order);
        s.setVisible(true);
        return s;
    }

    private void seedExperience() {
        if (experienceRepository.count() == 0) {
            Experience e = new Experience();
            e.setCompany("Codveda Technologies");
            e.setRole("Full Stack Developer Intern");
            e.setDuration("Jan 2026 - Feb 2026");
            e.setDescription(
                    "Built and shipped full-stack web features end-to-end using React.js, Node.js, Express.js, and MongoDB\n" +
                    "Designed and implemented RESTful APIs, integrating them with frontend interfaces for a seamless user experience\n" +
                    "Managed MySQL and MongoDB databases, handling data modeling, query optimization, and storage design");
            e.setType("Internship");
            e.setSortOrder(1);
            e.setVisible(true);
            experienceRepository.save(e);
        }
    }

    private void seedProjects() {
        if (projectRepository.count() == 0 && adminRepository.count() == 0) {
            Project p1 = new Project();
            p1.setTitle("POS Billing System");
            p1.setDescription("A full-stack Point of Sale application with invoice generation, cart management, " +
                    "and automatic billing calculations.");
            p1.setFeatures("Invoice generation\nCart management\nAutomatic billing calculations\nProduct catalog management");
            p1.setTechStack("Node.js,Express.js,MongoDB,HTML,CSS,JavaScript");
            p1.setGithubUrl("https://github.com/princegupt1234");
            p1.setFeatured(true);
            p1.setStatus("Completed");
            p1.setTags("Full Stack,Node.js,MongoDB");
            p1.setEngineeringHighlight("Engineered real-time billing calculations with optimistic cart state & MongoDB indexing");
            p1.setSortOrder(1);
            p1.setVisible(true);
            projectRepository.save(p1);

            Project p2 = new Project();
            p2.setTitle("Travel Booking Website");
            p2.setDescription("A full-stack travel platform enabling destination browsing and booking, with " +
                    "MySQL-backed user management.");
            p2.setFeatures("Destination browsing\nBooking flow\nUser authentication\nMySQL-backed data layer");
            p2.setTechStack("HTML,CSS,JavaScript,PHP,MySQL");
            p2.setGithubUrl("https://github.com/princegupt1234");
            p2.setFeatured(true);
            p2.setStatus("Completed");
            p2.setTags("Full Stack,PHP,MySQL");
            p2.setEngineeringHighlight("Designed normalized relational schema with session authentication and transactional booking flow");
            p2.setSortOrder(2);
            p2.setVisible(true);
            projectRepository.save(p2);

            Project p3 = new Project();
            p3.setTitle("Personal Portfolio");
            p3.setDescription("A responsive personal portfolio designed and deployed using Next.js and TypeScript, " +
                    "publicly hosted on Netlify.");
            p3.setFeatures("Responsive design\nServer-rendered pages\nDeployed on Netlify");
            p3.setTechStack("Next.js,React,TypeScript");
            p3.setLiveUrl("https://princesportfolio.netlify.app");
            p3.setFeatured(false);
            p3.setStatus("Live");
            p3.setTags("Frontend,Next.js");
            p3.setEngineeringHighlight("Static site generation with strict TypeScript types, modern glass design, and Netlify CI/CD");
            p3.setSortOrder(3);
            p3.setVisible(true);
            projectRepository.save(p3);
        } else {
            projectRepository.findAll().forEach(p -> {
                if (p.getEngineeringHighlight() == null || p.getEngineeringHighlight().isBlank()) {
                    if (p.getTitle().contains("POS")) {
                        p.setEngineeringHighlight("Engineered real-time billing calculations with optimistic cart state & MongoDB indexing");
                        projectRepository.save(p);
                    } else if (p.getTitle().contains("Travel")) {
                        p.setEngineeringHighlight("Designed normalized relational schema with session authentication and transactional booking flow");
                        projectRepository.save(p);
                    } else if (p.getTitle().contains("Portfolio")) {
                        p.setEngineeringHighlight("Static site generation with strict TypeScript types, modern glass design, and Netlify CI/CD");
                        projectRepository.save(p);
                    }
                }
            });
        }
    }

    private void seedCertificates() {
        if (certificateRepository.count() == 0) {
            Certificate c = new Certificate();
            c.setTitle("Full Stack Development Internship");
            c.setOrganization("Codveda Technologies");
            c.setIssueDate(LocalDate.of(2026, 3, 1));
            c.setCredentialId("CV/A1/55948");
            c.setSortOrder(1);
            c.setVisible(true);
            certificateRepository.save(c);
        }
    }

    private void seedServices() {
        if (serviceItemRepository.count() == 0) {
            List.of(
                    svc("Java Development", "Robust, well-structured Java applications using OOP best practices.", "fa-solid fa-code", 1),
                    svc("Spring Boot API", "RESTful backend services and APIs built with Spring Boot.", "fa-solid fa-server", 2),
                    svc("Responsive Website", "Pixel-perfect, mobile-first websites that work on every device.", "fa-solid fa-mobile-screen", 3),
                    svc("Database Design", "Efficient relational and document data models with MySQL and MongoDB.", "fa-solid fa-database", 4),
                    svc("Backend Development", "End-to-end backend features, from data modeling to API delivery.", "fa-solid fa-gears", 5)
            ).forEach(serviceItemRepository::save);
        }
    }

    private ServiceItem svc(String title, String desc, String icon, int order) {
        ServiceItem s = new ServiceItem();
        s.setTitle(title);
        s.setDescription(desc);
        s.setIcon(icon);
        s.setSortOrder(order);
        return s;
    }

    private void seedTestimonials() {
        if (testimonialRepository.count() == 0) {
            Testimonial t = new Testimonial();
            t.setName("Codveda Technologies");
            t.setRole("Internship Supervisor");
            t.setRating(5);
            t.setComment("Prince shipped full-stack features end-to-end during his internship, from REST APIs " +
                    "to database design, with strong ownership and attention to detail.");
            t.setPublished(true);
            testimonialRepository.save(t);
        }
    }
}
