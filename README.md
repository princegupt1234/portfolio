# Prince Gupt — Dark Glassmorphism Portfolio + Admin Panel

A Spring Boot (Java 21) + Thymeleaf + MySQL full-stack portfolio site with a complete,
password-protected admin panel, built for Prince Gupt (Full Stack Developer / Final Year CSE).

## Stack
- Java 21, Spring Boot 4.1
- Spring MVC + Thymeleaf (server-rendered)
- Spring Data JPA + MySQL
- Spring Security (form login, BCrypt passwords)
- Plain CSS (dark glassmorphism theme) + vanilla JS — no build step required
- Font Awesome (CDN), Chart.js (CDN, admin analytics only)

## 1. Prerequisites
- JDK 21+
- Maven (or use the included `./mvnw`)
- A running MySQL server (8.x recommended)

## 2. Database setup
The app auto-creates the schema/tables on startup (`spring.jpa.hibernate.ddl-auto=update`
and `createDatabaseIfNotExist=true`), so you only need MySQL **running** — you don't need
to manually create the database.

Edit `src/main/resources/application.properties` and set your real credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## 3. Run it

```bash
./mvnw spring-boot:run
```

or build a jar:

```bash
./mvnw clean package
java -jar target/portfolio-0.0.1-SNAPSHOT.jar
```

The app starts on **http://localhost:8080**

- Public portfolio: `http://localhost:8080/`
- Admin panel: `http://localhost:8080/admin/login`

## 4. Default admin login
On first boot, a default admin account and all of Prince's resume content (About,
Education, Skills, Experience, Projects, one Certificate, Services, a Testimonial) are
seeded automatically — editable from the admin panel afterwards.

```
Username: admin
Password: Prince@Admin123
```

**Change this password immediately** from Admin → Profile / Settings after your first login,
or override the defaults before first run via `application.properties`:

```properties
app.admin.default-username=yourname
app.admin.default-password=SomethingStronger123!
app.admin.default-email=you@example.com
```

## 5. File uploads (images, PDFs, resume)
Uploaded files (project images, certificate images/PDFs, testimonial photos, profile
photo, resume PDFs) are stored on disk under `./uploads/` (created automatically,
relative to wherever you run the jar from) and served at `/uploads/**`. This folder is
already in `.gitignore`.

## 6. Contact form email notifications (optional)
Disabled by default. To get an email whenever someone submits the contact form, set:

```properties
app.mail.enabled=true
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_gmail_app_password
```
(Use a Gmail **App Password**, not your normal password — Google requires 2FA + an app
password for SMTP.) Contact messages are always saved to the database and visible in
Admin → Contact Messages regardless of whether email is enabled.

## 7. GitHub / LeetCode live stats (optional, needs internet)
The "GitHub" and "LeetCode" sections on the public site call:
- `https://api.github.com/users/{username}` (official GitHub API, no key needed for
  this basic public data)
- `https://leetcode-stats-api.herokuapp.com/{username}` (a public unofficial mirror)

Set your real usernames in **Admin → About → GitHub Username / LeetCode Username**.
If the server has no internet access, or the third-party LeetCode mirror is down, those
sections simply show a friendly "not connected" message instead of breaking the page.

## 8. What's implemented

**Public site (single page, all in `templates/index.html`):**
Sticky glass navbar · animated hero with typing effect · About + education timeline ·
categorized/filterable skills with animated progress bars · experience timeline ·
project cards (GitHub/live links, click tracking) · live GitHub stats · live LeetCode
stats · certificate carousel with modal preview · services grid · testimonials slider ·
contact form (saves to DB + optional email) + Google Map embed · footer + back-to-top.

**Admin panel (`/admin/**`, login required):**
Dashboard with KPIs, a 30-day visitors chart, and quick actions · full CRUD for About
(+ education timeline), Skills, Experience, Projects (image upload, featured toggle),
Certificates (image + PDF upload), Services, Testimonials (photo upload, publish toggle)
· Contact inbox with search, reply notes, CSV export, unread badges · Resume version
history with download counts and one-click "make active" · Admin profile/password
settings · Analytics page (daily breakdown table + charts).

## 9. Notes / next steps you may want
- The color/design tokens live in `static/css/theme.css` (`:root` variables) — change
  once, applies everywhere.
- Drag-and-drop skill reordering and a rich-text editor for descriptions were **not**
  implemented (plain "Sort Order" number fields are used instead) — say the word if you
  want those added.
- Country-level visitor analytics (from the original spec) needs a geo-IP service and
  wasn't wired up — the current Analytics page tracks views/downloads/clicks/messages
  per day, which covers most of what a one-person portfolio actually needs.
- This project was written directly against Spring Boot 4.1 / Spring Security 6 APIs but
  could **not** be compiled or run in the sandbox that generated it (no internet access
  to download Maven dependencies) — please run `./mvnw clean package` yourself as the
  first step and let me know if anything doesn't compile so it can be fixed quickly.
