package com.example.portfolio.config;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.Admin;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.BlogPost;
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
import com.example.portfolio.repository.BlogPostRepository;
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
    private final BlogPostRepository blogPostRepository;
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
                            BlogPostRepository blogPostRepository,
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
        this.blogPostRepository = blogPostRepository;
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
        seedBlogs();
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
            info.setBlogSectionVisible(true);
            info.setBlogSectionTitle("Technical Writing & Deep Dives");
            info.setBlogSectionSubtitle("Architectural patterns, Spring Boot internals, and algorithmic roadmaps.");
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
                if (info.getBlogSectionVisible() == null) {
                    info.setBlogSectionVisible(true);
                    changed = true;
                }
                if (info.getBlogSectionTitle() == null || info.getBlogSectionTitle().isBlank()) {
                    info.setBlogSectionTitle("Technical Writing & Deep Dives");
                    changed = true;
                }
                if (info.getBlogSectionSubtitle() == null || info.getBlogSectionSubtitle().isBlank()) {
                    info.setBlogSectionSubtitle("Architectural patterns, Spring Boot internals, and algorithmic roadmaps.");
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
        if (projectRepository.count() == 0) {
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

    private void seedBlogs() {
        if (blogPostRepository.count() == 0) {
            BlogPost b1 = new BlogPost();
            b1.setTitle("Deep Dive: Eliminating Hibernate N+1 Query Problems in Spring Boot");
            b1.setSlug("eliminating-hibernate-n-plus-1-queries-spring-boot");
            b1.setSummary("How unnoticed lazy loading issues silently degrade Spring Boot API response times, and step-by-step techniques to eliminate them using JOIN FETCH, Entity Graphs, and batch fetching.");
            b1.setTags("Java, Spring Boot, Hibernate, MySQL, Performance");
            b1.setReadTimeMinutes(6);
            b1.setFeatured(true);
            b1.setPublished(true);
            b1.setContent("### The Silent Performance Killer in Relational Backends\n\n"
                    + "When developing RESTful APIs with **Spring Boot** and **Spring Data JPA**, one of the most frequent performance bottlenecks is the infamous **N+1 Query Problem**.\n\n"
                    + "It typically occurs when you have a `@OneToMany` or `@ManyToOne` relationship with `FetchType.LAZY`. While lazy loading prevents fetching excessive data on initialization, naive loops over child entities trigger an additional SQL SELECT for every single parent record!\n\n"
                    + "```java\n"
                    + "// If there are 100 departments, this triggers 1 + 100 queries!\n"
                    + "List<Department> departments = departmentRepository.findAll();\n"
                    + "for (Department dept : departments) {\n"
                    + "    System.out.println(dept.getEmployees().size());\n"
                    + "}\n"
                    + "```\n\n"
                    + "### Solution 1: JPQL JOIN FETCH\n"
                    + "By writing an explicit `JOIN FETCH` query, Hibernate eagerly fetches both the parent and children in a single SQL `INNER/LEFT JOIN`:\n\n"
                    + "```java\n"
                    + "@Query(\"SELECT d FROM Department d LEFT JOIN FETCH d.employees\")\n"
                    + "List<Department> findAllWithEmployees();\n"
                    + "```\n\n"
                    + "### Solution 2: JPA Entity Graphs\n"
                    + "JPA 2.1 introduces `@EntityGraph`, which allows declarative fetching without hardcoding custom JPQL joins everywhere:\n\n"
                    + "```java\n"
                    + "@EntityGraph(attributePaths = {\"employees\"})\n"
                    + "List<Department> findAll();\n"
                    + "```\n\n"
                    + "### Solution 3: Batch Fetching (`default_batch_fetch_size`)\n"
                    + "Setting `spring.jpa.properties.hibernate.default_batch_fetch_size=30` instructs Hibernate to batch secondary lookups into `WHERE id IN (?, ?, ...)` instead of firing individual queries.\n\n"
                    + "### Conclusion\n"
                    + "Monitoring SQL statements in staging and leveraging `JOIN FETCH` or `EntityGraph` keeps API response times predictable (sub-50ms) even under high concurrency.");
            blogPostRepository.save(b1);

            BlogPost b2 = new BlogPost();
            b2.setTitle("My Roadmap to Solving 350+ LeetCode Problems: Patterns over Memorization");
            b2.setSlug("roadmap-solving-350-plus-leetcode-problems-patterns");
            b2.setSummary("Why blindly grinding 500+ problems fails, and how categorizing problems into core algorithmic archetypes transformed my problem-solving clarity.");
            b2.setTags("DSA, LeetCode, Algorithms, Problem Solving, Java");
            b2.setReadTimeMinutes(7);
            b2.setFeatured(true);
            b2.setPublished(true);
            b2.setContent("### The Trap of Grinding Without Structure\n\n"
                    + "When I began competitive programming and interview preparation, I fell into the common trap of randomly solving whatever appeared on the daily challenge. After 60 problems, I still froze whenever a problem had an unfamiliar story wrapper.\n\n"
                    + "The shift happened when I stopped counting problems and started categorizing by **algorithmic patterns**.\n\n"
                    + "### The 6 Foundational Patterns That Changed Everything\n\n"
                    + "1. **Two Pointers & Sliding Window**: For sorted arrays and contiguous subarray problems (e.g., Longest Substring Without Repeating Characters).\n"
                    + "2. **Fast & Slow Pointers**: Floyd's Cycle Detection for linked lists and cyclic arrays.\n"
                    + "3. **Monotonic Stack**: For 'Next Greater Element' or temperature ranges in $O(N)$ instead of nested $O(N^2)$ loops.\n"
                    + "4. **BFS vs DFS on Graphs & Trees**: Level-order traversal for shortest unweighted paths vs recursion for component connectivity.\n"
                    + "5. **Backtracking with Pruning**: Generating permutations, combinations, and N-Queens with state restoration.\n"
                    + "6. **Dynamic Programming**: Identifying sub-problems and choosing between Top-Down Memoization vs Bottom-Up Tabulation.\n\n"
                    + "### Translating DSA to Real-World Engineering\n\n"
                    + "Mastering these patterns doesn't just pass technical interviews—it instills an instinctive understanding of space/time trade-offs, enabling you to write memory-efficient algorithms and avoid accidental $O(N^2)$ processing in production backend services.");
            blogPostRepository.save(b2);

            BlogPost b3 = new BlogPost();
            b3.setTitle("Architecting Stateless JWT Authentication with Role-Based Access in Spring Security 6");
            b3.setSlug("stateless-jwt-authentication-spring-security-6");
            b3.setSummary("A complete production architecture guide for modern Spring Security 6: building stateless token verification, handling expired token refresh, and enforcing declarative role boundaries.");
            b3.setTags("Spring Security, JWT, Authentication, Architecture, REST API");
            b3.setReadTimeMinutes(5);
            b3.setFeatured(false);
            b3.setPublished(true);
            b3.setContent("### Moving Away from Deprecated Adapters\n\n"
                    + "In Spring Security 6 (Spring Boot 3), `WebSecurityConfigurerAdapter` is completely gone. Modern applications define component-based `SecurityFilterChain` beans with functional lambda DSLs.\n\n"
                    + "### Architecture Overview\n\n"
                    + "A robust token authentication flow consists of:\n\n"
                    + "1. **`JwtAuthenticationFilter`**: Intercepts HTTP requests, extracts the `Authorization: Bearer <token>` header, validates the HMAC-SHA256 signature, and populates `SecurityContextHolder`.\n"
                    + "2. **`UserDetailsService`**: Loads user credentials and granted authorities (`ROLE_ADMIN`, `ROLE_USER`) from the relational database.\n"
                    + "3. **`SessionCreationPolicy.STATELESS`**: Ensures the server stores zero HTTP session state in memory, allowing effortless horizontal scaling across multiple container instances.\n\n"
                    + "```java\n"
                    + "@Bean\n"
                    + "public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {\n"
                    + "    return http\n"
                    + "        .csrf(csrf -> csrf.disable())\n"
                    + "        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n"
                    + "        .authorizeHttpRequests(auth -> auth\n"
                    + "            .requestMatchers(\"/api/auth/**\").permitAll()\n"
                    + "            .requestMatchers(\"/admin/**\").hasRole(\"ADMIN\")\n"
                    + "            .anyRequest().authenticated()\n"
                    + "        )\n"
                    + "        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)\n"
                    + "        .build();\n"
                    + "}\n"
                    + "```\n\n"
                    + "### Production Hardening Tips\n\n"
                    + "• Store secrets in environment variables (`${JWT_SECRET}`), never hardcoded.\n"
                    + "• Keep access token expiry short (15-60 mins) and issue secure HttpOnly refresh tokens for renew cycles.");
            blogPostRepository.save(b3);
        }
    }
}
