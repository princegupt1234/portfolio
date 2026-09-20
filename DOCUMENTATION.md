# 📚 Prince Gupt - Full Stack Java Portfolio & Admin Panel — Complete Documentation

A modern, production-grade portfolio and Content Management System (CMS) engineered with **Java 21**, **Spring Boot 4.1**, **Spring Security 6**, **Spring Data JPA**, **MySQL**, **Thymeleaf**, and **Vanilla JavaScript & CSS**.

---

## 📑 Table of Contents
1. [Overview & Architecture](#1-overview--architecture)
2. [Quick Start & Local Setup](#2-quick-start--local-setup)
3. [Core Systems & Features](#3-core-systems--features)
   - [Interactive Developer CLI Terminal](#interactive-developer-cli-terminal)
   - [Dual-Engine "Ask Prince AI" Chatbot](#dual-engine-ask-prince-ai-chatbot)
   - [Recruiter 30-Second Briefing Modal](#recruiter-30-second-briefing-modal)
   - [Live LeetCode & GitHub Stats Sync](#live-leetcode--github-stats-sync)
   - [Anti-Spam & Contact Form Security](#anti-spam--contact-form-security)
   - [Resume Management & PDF Streaming](#resume-management--pdf-streaming)
   - [Real-Time Frontend Sync](#real-time-frontend-sync)
   - [Progressive Web App (PWA) & Open Graph SEO](#progressive-web-app-pwa--open-graph-seo)
4. [Admin Panel Guide](#4-admin-panel-guide)
5. [Database Schema & Data Model](#5-database-schema--data-model)
6. [Security Architecture](#6-security-architecture)
7. [API & Endpoints Reference](#7-api--endpoints-reference)
8. [Configuration & Environment Variables](#8-configuration--environment-variables)
9. [Deployment & Containerization](#9-deployment--containerization)
10. [Testing & Quality Assurance](#10-testing--quality-assurance)
11. [Troubleshooting & FAQs](#11-troubleshooting--faqs)

---

## 1. Overview & Architecture

### Tech Stack
- **Backend**: Java 21, [Spring Boot 4.1.0](file:///c:/Users/princ/Desktop/trail/portfolio/pom.xml) (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-mail`)
- **Template Engine**: Thymeleaf 3 with `thymeleaf-extras-springsecurity6`
- **Database & Persistence**: MySQL 8.x, Hibernate 6, HikariCP Connection Pool
- **Frontend**: Vanilla JavaScript (ES6+), Modern CSS3 Design Tokens (`theme.css`), FontAwesome 6, Google Fonts (Poppins, Inter, JetBrains Mono)
- **AI Integration**: Google Generative Language REST API (`gemini-2.0-flash`, `gemini-1.5-flash`) with internal rule-based domain fallback
- **Containerization**: Multi-stage Docker build with Eclipse Temurin 21 JRE

### System Architecture Diagram
```
┌────────────────────────────────────────────────────────────────────────┐
│                        Client Browser (Desktop / Mobile / PWA)         │
└────────────────────────────────────┬───────────────────────────────────┘
                                     │ HTTP/HTTPS
                                     ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 Spring Security 6 (RBAC, CSRF, BCrypt)                  │
└───────┬────────────────────────────┬────────────────────────────┬──────┘
        │                            │                            │
        ▼                            ▼                            ▼
┌───────────────────┐      ┌───────────────────┐      ┌──────────────────┐
│  HomeController   │      │  AiChatController │      │ AdminControllers │
│  Public Views     │      │  /api/ai/chat     │      │ 14 Management    │
│  Contact & Resume │      │                   │      │ Controllers      │
└─────────┬─────────┘      └─────────┬─────────┘      └─────────┬────────┘
          │                          │                          │
          ▼                          ▼                          ▼
┌────────────────────────────────────────────────────────────────────────┐
│                             Service Layer                              │
│ ┌──────────────────────────┐ ┌───────────────────────────────────────┐ │
│ │ PortfolioAiService       │ │ ContactRateLimiterService             │ │
│ │ (Gemini API + Fallback)  │ │ (IP Cooldown & Proxy Header Parsing)  │ │
│ ├──────────────────────────┤ ├───────────────────────────────────────┤ │
│ │ LeetCodeStatsService     │ │ MailService                           │ │
│ │ (GraphQL + Proxy + Cache)│ │ (Contact Alerts & Direct Email Reply) │ │
│ ├──────────────────────────┤ ├───────────────────────────────────────┤ │
│ │ AnalyticsService         │ │ FileStorageService                    │ │
│ │ (In-House Metrics)       │ │ (Sanitized UUID Disk Storage)         │ │
│ └──────────────────────────┘ └───────────────────────────────────────┘ │
└────────────────────────────────────┬───────────────────────────────────┘
                                     ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     Spring Data JPA Repositories                       │
└────────────────────────────────────┬───────────────────────────────────┘
                                     ▼
┌────────────────────────────────────────────────────────────────────────┐
│                          MySQL 8.x Database                            │
└────────────────────────────────────────────────────────────────────────┘
```

### Project Directory Layout
```
portfolio/
├── pom.xml                                    # Maven dependencies & build config
├── Dockerfile                                 # Multi-stage container deployment
├── README.md                                  # Quick-start documentation
├── DOCUMENTATION.md                           # Comprehensive technical manual
├── src/
│   ├── main/
│   │   ├── java/com/example/portfolio/
│   │   │   ├── PortfolioApplication.java     # Spring Boot application entrypoint
│   │   │   ├── config/                       # Security, Web MVC, and Data Seeding
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── GlobalBindingInitializer.java
│   │   │   ├── controller/                   # Public-facing web & REST controllers
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── AiChatController.java
│   │   │   │   ├── ApiController.java
│   │   │   │   ├── PwaController.java
│   │   │   │   └── admin/                    # 18 Admin panel controllers
│   │   │   │       ├── AdminDashboardController.java
│   │   │   │       ├── AdminSectionController.java
│   │   │   │       ├── AdminHeroController.java
│   │   │   │       ├── AdminAboutController.java
│   │   │   │       ├── AdminHiringController.java
│   │   │   │       ├── AdminInteractiveController.java
│   │   │   │       ├── AdminSkillController.java
│   │   │   │       ├── AdminProjectController.java
│   │   │   │       ├── AdminExperienceController.java
│   │   │   │       ├── AdminCertificateController.java
│   │   │   │       ├── AdminServiceItemController.java
│   │   │   │       ├── AdminTestimonialController.java
│   │   │   │       ├── AdminMessageController.java
│   │   │   │       ├── AdminResumeController.java
│   │   │   │       ├── AdminAnalyticsController.java
│   │   │   │       ├── AdminProfileController.java
│   │   │   │       ├── AdminLoginController.java
│   │   │   │       └── AdminGlobalModelAdvice.java
│   │   │   ├── entity/                       # 14 JPA domain entities
│   │   │   │   ├── AboutInfo.java
│   │   │   │   ├── Admin.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── Skill.java
│   │   │   │   ├── Experience.java
│   │   │   │   ├── EducationEntry.java
│   │   │   │   ├── Certificate.java
│   │   │   │   ├── ContactMessage.java
│   │   │   │   ├── SiteStat.java
│   │   │   │   ├── Resume.java
│   │   │   │   ├── BuildingProject.java
│   │   │   │   ├── LearningProject.java
│   │   │   │   ├── ServiceItem.java
│   │   │   │   └── Testimonial.java
│   │   │   ├── repository/                   # 14 Spring Data repositories
│   │   │   ├── service/                      # Business logic & external API clients
│   │   │   │   ├── PortfolioAiService.java
│   │   │   │   ├── LeetCodeRepositoryStatsService.java
│   │   │   │   ├── GithubStatsService.java
│   │   │   │   ├── ContactRateLimiterService.java
│   │   │   │   ├── MailService.java
│   │   │   │   ├── AnalyticsService.java
│   │   │   │   ├── FileStorageService.java
│   │   │   │   ├── DataVersionService.java
│   │   │   │   └── ProjectService.java
│   │   │   └── dto/                          # Form validation & request DTOs
│   │   └── resources/
│   │       ├── application.properties        # App properties & env variable bindings
│   │       ├── static/                       # Static public assets
│   │       │   ├── css/                      # theme.css, public.css, admin.css
│   │       │   ├── js/                       # main.js, admin.js
│   │       │   └── icons/                    # PWA icons & favicon
│   │       └── templates/                    # Thymeleaf templates
│   │           ├── index.html                # Main public single-page portfolio
│   │           └── admin/                    # Admin panel views & fragments
│   └── test/                                 # Unit and integration test suites
```

---

## 2. Quick Start & Local Setup

### Prerequisites
- **JDK 21+** installed and available on `PATH`.
- **MySQL 8.x** running locally (or via Docker).
- **Maven** (or use the included `./mvnw` wrapper).

### Step 1: Configure MySQL
Create a database named `portfolio_db`:
```sql
CREATE DATABASE portfolio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

In [`src/main/resources/application.properties`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/resources/application.properties), configure your database credentials or export environment variables:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 2: Run Application
Using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
On Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

The application will start on port `8080`:
- **Public Portfolio**: [http://localhost:8080/](http://localhost:8080/)
- **Admin Panel Login**: [http://localhost:8080/admin/login](http://localhost:8080/admin/login)

### Step 3: Default Admin Credentials
On initial startup, [`DataInitializer.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/config/DataInitializer.java) automatically seeds the database with initial portfolio content and a default admin account:
- **Username**: `admin` (or configured via `ADMIN_USERNAME`)
- **Password**: `admin123` (or configured via `ADMIN_PASSWORD`)

> [!IMPORTANT]
> Change the default password immediately after first login from **Admin → Profile & Settings** (`/admin/profile`).

---

## 3. Core Systems & Features

### Interactive Developer CLI Terminal
- **Access**: Press `Ctrl + ~` anywhere on the site or click the floating **CLI** launcher button.
- **Features**:
  - Auto-complete via `Tab` key.
  - Command history using `Up` / `Down` arrow keys.
  - Built-in commands: `help`, `skills`, `projects`, `experience`, `hire`, `leetcode`, `arch`, `system`, `resume`, `contact`, `theme`, `clear`, and `matrix` (animated digital rain effect).
  - **Dynamic Command Manager**: Custom terminal commands can be created, edited, and deleted from **Admin → AI & Interactive Tools** without code changes.

### Dual-Engine "Ask Prince AI" Chatbot
Implemented in [`PortfolioAiService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/PortfolioAiService.java) and exposed via [`AiChatController.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/controller/AiChatController.java):
1. **Google Gemini API**: Uses `gemini-2.0-flash` (or `gemini-1.5-flash`) to generate accurate, professional answers. Enforces strict zero-hallucination guardrails based on live database records.
2. **Local Domain Fallback Engine**: If no API key is provided or if network latency spikes, an intelligent regex and keyword engine answers recruiter inquiries about DSA problem-solving counts, verified project stacks, availability, and contact details with zero downtime.

### Recruiter 30-Second Briefing Modal
Implemented in [`index.html`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/resources/templates/index.html#L842):
- Presents an executive snapshot tailored for engineering managers and talent acquisition teams:
  - Immediate availability badge (0-day notice).
  - Live LeetCode solved metrics.
  - Core technical competencies (Java, Spring Boot, MySQL, REST APIs, Microservices).
  - One-click **"Copy Pitch"** button copying a structured Markdown summary directly to the recruiter's clipboard.

### Live LeetCode & GitHub Stats Sync
Implemented in [`LeetCodeRepositoryStatsService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/LeetCodeRepositoryStatsService.java):
- **Zero Latency**: Employs an in-memory `AtomicReference` cache so public page loads take 0ms for statistics.
- **Asynchronous Refresh**: Updates asynchronously using a three-tier fallback:
  1. Official LeetCode GraphQL API (`https://leetcode.com/graphql`).
  2. Vercel REST Proxy (`https://leetcode-api-faisalshohag.vercel.app/`).
  3. Pre-seeded verified statistics.

### Anti-Spam & Contact Form Security
Implemented in [`HomeController.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/controller/HomeController.java#L167) and [`ContactRateLimiterService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/ContactRateLimiterService.java):
- **Honeypot Trap**: Invisible field `websiteTrap`. Bot submissions are silently discarded without saving or emailing.
- **IP Rate Limiter**: Inspects `X-Forwarded-For` and `X-Real-IP` behind reverse proxies (Render, Cloudflare) and applies a configurable cooldown (default 60 seconds).
- **In-App Direct Reply**: Admins can reply to messages directly from `/admin/messages` using branded HTML email templates ([`MailService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/MailService.java)).

### Resume Management & PDF Streaming
- Streaming endpoint at `/resume/preview` streams the active PDF inline with HTTP headers (`Cache-Control: public, max-age=3600`), rendering directly within an in-page modal iframe.
- Endpoint at `/resume/download` increments the download counter, updates daily analytics, and triggers a file download attachment.

### Real-Time Frontend Sync
- Every admin update increments an in-memory version in [`DataVersionService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/DataVersionService.java).
- The public frontend polls `/api/data-version` every 30 seconds. If a change occurs in the admin panel, visitor tabs reload automatically without requiring a hard refresh.

### Progressive Web App (PWA) & Open Graph SEO
- Dynamically generates `/manifest.webmanifest` via [`PwaController.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/controller/PwaController.java) using settings from the admin panel.
- Dynamic Open Graph and Twitter Card tags configured in [`AboutInfo.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/AboutInfo.java) ensure rich preview cards when shared on LinkedIn, WhatsApp, or X/Twitter.

---

## 4. Admin Panel Guide

The admin panel is accessible at `/admin` (protected by Spring Security).

```
Admin Control Center
├── Dashboard (/admin/dashboard)           - KPI summary, 30-day analytics chart, quick links
├── Section Visibility (/admin/sections)   - Toggle any public section on/off
├── Hero Section (/admin/hero)             - Typing phrases, speeds, intro bio, CTA links
├── About & Story (/admin/about)           - Personal story, education, currently learning
├── Hiring & Availability (/admin/hiring)  - Work preferences, notice period, target roles
├── AI & Interactive (/admin/interactive)  - Gemini chatbot settings, custom CLI commands, SEO
├── Skills (/admin/skills)                 - Category-grouped technical competencies & %
├── Experience (/admin/experience)         - Career timeline & bullet points
├── Projects (/admin/projects)             - Projects catalog, live demos, architecture diagrams
├── Certificates (/admin/certificates)     - Credentials & verification URLs
├── Services (/admin/services)             - Professional service offerings
├── Testimonials (/admin/testimonials)     - Peer & mentor recommendations
├── Messages (/admin/messages)             - Inquiries inbox & direct email composer
├── Resume (/admin/resume)                 - Upload, preview, and set active PDF
├── Analytics (/admin/analytics)           - Detailed 30-day view counts, clicks, and downloads
└── Profile & Account (/admin/profile)     - Admin username, email, and password changes
```

---

## 5. Database Schema & Data Model

The application uses 14 JPA entities mapped to MySQL:

| Entity | Table Name | Key Fields | Description |
|---|---|---|---|
| [`AboutInfo`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/AboutInfo.java) | `about_info` | `fullName`, `title`, `bio`, `heroPhrases`, `hiringRoles`, `availabilityText`, `customCliCommands`, section flags | Core profile and dynamic feature toggles (singleton record). |
| [`Admin`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Admin.java) | `admins` | `username`, `password`, `email`, `role` | Administrator credentials (passwords stored as BCrypt hashes). |
| [`Project`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Project.java) | `projects` | `title`, `description`, `techStack`, `githubUrl`, `liveUrl`, `architectureImageUrl`, `sortOrder`, `featured` | Portfolio projects catalog. |
| [`Skill`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Skill.java) | `skills` | `name`, `category`, `proficiency`, `iconClass`, `visible`, `sortOrder` | Technical skills with percentage proficiency. |
| [`Experience`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Experience.java) | `experiences` | `role`, `company`, `duration`, `description`, `sortOrder`, `visible` | Career history and internships. |
| [`EducationEntry`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/EducationEntry.java) | `education_entries` | `degree`, `institution`, `score`, `year`, `sortOrder` | Educational background. |
| [`Certificate`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Certificate.java) | `certificates` | `title`, `issuer`, `issueDate`, `credentialUrl`, `imageUrl`, `visible` | Verified certifications and badges. |
| [`Resume`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Resume.java) | `resumes` | `fileName`, `fileUrl`, `active`, `downloadCount` | Stored resume versions. |
| [`ContactMessage`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/ContactMessage.java) | `contact_messages` | `name`, `email`, `subject`, `message`, `isRead`, `repliedAt`, `createdAt` | Messages submitted through the contact form. |
| [`SiteStat`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/SiteStat.java) | `site_stats` | `statDate` (UNIQUE), `portfolioViews`, `resumeDownloads`, `messagesReceived`, `projectClicks` | Daily analytics counters. |
| [`BuildingProject`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/BuildingProject.java) | `building_projects` | `title`, `description`, `techStack`, `progress`, `status` | "Currently Building" showcase cards. |
| [`LearningProject`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/LearningProject.java) | `learning_projects` | `title`, `description`, `category`, `status` | "Currently Learning" roadmap items. |
| [`ServiceItem`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/ServiceItem.java) | `service_items` | `title`, `description`, `iconClass`, `sortOrder`, `visible` | Professional offerings. |
| [`Testimonial`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/entity/Testimonial.java) | `testimonials` | `name`, `role`, `company`, `content`, `rating`, `published` | Recommendations and peer reviews. |

---

## 6. Security Architecture

Configured in [`SecurityConfig.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/config/SecurityConfig.java):

```java
// Highlights from SecurityConfig.java
http
    .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin/login", "/css/**", "/js/**", "/images/**",
                "/uploads/**", "/", "/about", "/skills", "/experience",
                "/projects", "/projects/**", "/certificates", "/services", "/testimonials",
                "/contact", "/contact/**", "/resume/download", "/resume/preview",
                "/api/data-version", "/api/skills", "/api/ai/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().permitAll()
    )
    .formLogin(form -> form
        .loginPage("/admin/login")
        .defaultSuccessUrl("/admin/dashboard", true)
        .failureUrl("/admin/login?error=true")
    )
    .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringRequestMatchers("/contact/**", "/api/ai/**")
    );
```

1. **Authentication**: Form-based authentication against database admins with BCrypt password hashing.
2. **CSRF Protection**: Cookie-based CSRF tokens (`XSRF-TOKEN`) for AJAX calls; stateless public APIs (`/api/ai/**`, `/contact/**`) are excluded.
3. **Clickjacking Defense**: Configured with `frameOptions().sameOrigin()` allowing only local iframe rendering for resume PDF previews.
4. **File Storage Safety**: In [`FileStorageService.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/java/com/example/portfolio/service/FileStorageService.java), original filenames are sanitized with `cleanPath` and saved under random `UUID` filenames to prevent directory traversal attacks.

---

## 7. API & Endpoints Reference

### Public Endpoints
| HTTP Method | Route | Description |
|---|---|---|
| `GET` | `/` | Renders the public portfolio homepage. |
| `POST` | `/contact/submit` | Submits a contact form inquiry (protected by honeypot & IP rate limiter). |
| `GET` | `/resume/preview` | Streams active resume PDF inline for browser viewing. |
| `GET` | `/resume/download` | Increments download count and serves resume PDF attachment. |
| `GET` | `/project/{id}/click` | Records a project link click for analytics. |
| `GET` | `/manifest.webmanifest` | Dynamically returns PWA web manifest. |
| `GET` | `/api/data-version` | Returns `{ "version": <timestamp> }` for live client refresh. |
| `GET` | `/api/skills` | Returns list of all visible technical skills in JSON. |
| `POST` | `/api/ai/chat` | Receives `{ "message": "..." }` and returns AI chatbot response. |

### Admin Endpoints (Requires `ROLE_ADMIN`)
| HTTP Method | Route | Description |
|---|---|---|
| `GET` | `/admin/dashboard` | Main admin overview and metrics. |
| `GET` / `POST` | `/admin/sections` | Section visibility controls. |
| `POST` | `/admin/sections/toggle` | AJAX toggle for individual section visibility. |
| `GET` / `POST` | `/admin/hero` | Hero text, typing animations, and CTA management. |
| `GET` / `POST` | `/admin/about` | Bio, story, education, and social links. |
| `GET` / `POST` | `/admin/hiring` | Hiring availability, target roles, and recruiter brief. |
| `GET` / `POST` | `/admin/interactive` | AI chatbot, terminal CLI, and SEO/OG settings. |
| `GET` / `POST` | `/admin/skills/**` | Skills CRUD operations. |
| `GET` / `POST` | `/admin/projects/**` | Projects catalog CRUD operations. |
| `GET` / `POST` | `/admin/experience/**` | Experience timeline CRUD operations. |
| `GET` / `POST` | `/admin/certificates/**` | Certificates CRUD operations. |
| `GET` / `POST` | `/admin/messages/**` | Message viewing, mark as read, delete, and email reply. |
| `GET` / `POST` | `/admin/resume/**` | Resume upload, activation, and deletion. |
| `GET` | `/admin/analytics` | 30-day analytics charts and raw logs. |
| `GET` / `POST` | `/admin/profile` | Update username, email, and change admin password. |

---

## 8. Configuration & Environment Variables

All settings are configured in [`src/main/resources/application.properties`](file:///c:/Users/princ/Desktop/trail/portfolio/src/main/resources/application.properties) with production-ready environment variable fallbacks:

```properties
# MySQL Connection
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/portfolio_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:1234}

# Admin Account Seeding
app.admin.default-username=${ADMIN_USERNAME:admin}
app.admin.default-password=${ADMIN_PASSWORD:admin123}
app.admin.default-email=${ADMIN_EMAIL:admin@example.com}

# HTTPS Email APIs (Recommended on Render - Port 443, immune to SMTP port blocks)
resend.api.key=${RESEND_API_KEY:}
resend.from=${RESEND_FROM:Prince Gupt <onboarding@resend.dev>}

brevo.api.key=${BREVO_API_KEY:}
brevo.sender.email=${BREVO_SENDER_EMAIL:${MAIL_USERNAME:princegupt3052@gmail.com}}
brevo.sender.name=${BREVO_SENDER_NAME:Prince Gupt}

# Traditional SMTP (Fallback if API keys are not provided)
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:princegupt3052@gmail.com}
spring.mail.password=${MAIL_PASSWORD:}
app.mail.notify-to=${MAIL_NOTIFY_TO:princegupt3052@gmail.com}
app.mail.enabled=${MAIL_ENABLED:true}

# AI Chatbot (Google Gemini)
gemini.api.key=${GEMINI_API_KEY:}
gemini.model=${GEMINI_MODEL:gemini-2.0-flash}

# Server Port (Auto-injected by Render)
server.port=${PORT:8080}
```

### Complete Environment Variables Table
| Variable | Required | Description | Example |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | Yes (in prod) | JDBC URL to MySQL database | `jdbc:mysql://host:3306/db?useSSL=true` |
| `SPRING_DATASOURCE_USERNAME` | Yes (in prod) | Database username | `admin` |
| `SPRING_DATASOURCE_PASSWORD` | Yes (in prod) | Database password | `secret_password` |
| `RESEND_API_KEY` | Recommended | Resend API Key for HTTPS email delivery on Render (Port 443) | `re_123456789abc` |
| `RESEND_FROM` | No | Custom verified sender or onboarding test sender | `Prince Gupt <onboarding@resend.dev>` |
| `BREVO_API_KEY` | No | Brevo API Key (alternative HTTPS email provider) | `xkeysib-abc...` |
| `ADMIN_USERNAME` | No | Default admin username on first seed | `prince` |
| `ADMIN_PASSWORD` | No | Default admin password on first seed | `StrongPassword123` |
| `ADMIN_EMAIL` | No | Default admin contact email | `prince@example.com` |
| `MAIL_USERNAME` | No | Gmail address for SMTP fallback | `you@gmail.com` |
| `MAIL_PASSWORD` | No | 16-character Google App Password (no spaces) | `abcdefghijklmnop` |
| `GEMINI_API_KEY` | No | Google AI Studio Gemini API Key | `AIzaSy...` |
| `PORT` | Auto | Web server listen port (provided by Render) | `8080` or `10000` |

---

## 9. Deployment & Containerization

### Docker Deployment
The project includes a multi-stage [`Dockerfile`](file:///c:/Users/princ/Desktop/trail/portfolio/Dockerfile):
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-XX:+UseSerialGC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

> [!TIP]
> Notice the JVM optimization flags `-Xms64m -Xmx256m -XX:+UseSerialGC`. These are tuned for low-memory container environments (such as Render's 512MB RAM free/starter instances), preventing Out-Of-Memory (OOM) container terminations.

### Deploying to Render
1. Push this repository to GitHub.
2. In the Render Dashboard, create a **New Web Service** and connect your repository.
3. Select **Docker** environment (Render automatically detects `./Dockerfile`).
4. Attach a MySQL database (e.g. Render MySQL, Aiven, or Railway).
5. Add the environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `ADMIN_PASSWORD`, etc.).
6. Click **Deploy**. Render will build the container and launch your service.

---

## 10. Testing & Quality Assurance

The project includes unit and integration tests using Spring Boot Test, MockMvc, and an in-memory H2 database:

- **Admin Controllers**:
  - [`AdminAboutControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminAboutControllerTest.java)
  - [`AdminHeroControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminHeroControllerTest.java)
  - [`AdminHiringControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminHiringControllerTest.java)
  - [`AdminInteractiveControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminInteractiveControllerTest.java)
  - [`AdminMessageControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminMessageControllerTest.java)
  - [`AdminProjectControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminProjectControllerTest.java)
  - [`AdminSectionControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/admin/AdminSectionControllerTest.java)
- **Services & Public APIs**:
  - [`PortfolioAiServiceTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/service/PortfolioAiServiceTest.java)
  - [`LeetCodeRepositoryStatsServiceTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/service/LeetCodeRepositoryStatsServiceTest.java)
  - [`ContactRateLimiterServiceTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/service/ContactRateLimiterServiceTest.java)
  - [`AiChatControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/AiChatControllerTest.java)
  - [`PwaControllerTest.java`](file:///c:/Users/princ/Desktop/trail/portfolio/src/test/java/com/example/portfolio/controller/PwaControllerTest.java)

Run the test suite with:
```bash
./mvnw test
```

---

## 11. Troubleshooting & FAQs

#### Q: The public page loads, but images or resume PDFs return 404 on Render after redeployment.
**A**: Render's free tier uses ephemeral containers. Any files uploaded to `/uploads` are cleared upon redeployment. To persist uploads, mount a **Render Disk** at `/app/uploads` and configure `app.upload.dir=/app/uploads`, or integrate S3/Cloudflare R2 object storage.

#### Q: Contact form emails are not sending.
**A**: Ensure you are using a Google **App Password** (16 characters without spaces) rather than your normal Google account password:
1. Enable 2-Factor Authentication on your Google account.
2. Go to **Google Account → Security → App Passwords**.
3. Generate a password for "Mail" and set it as `MAIL_PASSWORD`.

#### Q: How do I change the default admin credentials after initial startup?
**A**: Log in at `/admin/login`, navigate to **Profile & Settings** (`/admin/profile`), enter your current password, and specify your new username and password.

#### Q: The AI Chatbot shows fallback responses instead of Gemini responses.
**A**: Verify that `GEMINI_API_KEY` is set in your environment variables. You can obtain a free API key from [Google AI Studio](https://aistudio.google.com/). The local fallback domain engine guarantees the assistant continues answering portfolio questions accurately even without an active key.
