# Prince Gupt — Portfolio + Admin Panel

A full-stack portfolio site with a password-protected admin panel.  
**Live on Render:** [https://your-app.onrender.com](https://your-app.onrender.com)

**Stack:** Java 21 · Spring Boot 4.1 · Thymeleaf · Spring Security · Spring Data JPA · MySQL · Plain CSS (dark glassmorphism) · Vanilla JS

---

## Local Development

### Prerequisites
- JDK 21+
- Maven (or use `./mvnw`)
- MySQL 8.x running locally

### 1. Configure database

Edit `src/main/resources/application.properties` or set environment variables:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

The schema and tables are created automatically on first boot (`ddl-auto=update`).

### 2. Run

```bash
./mvnw spring-boot:run
```

- Public site: `http://localhost:8080/`
- Admin panel: `http://localhost:8080/admin/login`

---

## Deploy on Render

### Services needed
1. **Web Service** — Docker (this repo)
2. **MySQL Database** — Render managed MySQL, or any external MySQL (e.g. PlanetScale, Aiven, Railway)

### Step-by-step

1. Push this repo to GitHub.

2. In Render → **New Web Service** → connect your repo.
   - **Environment:** Docker
   - **Dockerfile path:** `./Dockerfile` (auto-detected)
   - **Instance type:** Free or Starter

3. Add the following **Environment Variables** in Render → Environment:

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Full JDBC URL to your MySQL | `jdbc:mysql://host:3306/portfolio_db?useSSL=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | MySQL username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | MySQL password | `yourpassword` |
| `ADMIN_USERNAME` | Admin login username | `admin` |
| `ADMIN_PASSWORD` | Admin login password | |
| `ADMIN_EMAIL` | Admin email | `you@example.com` |
| `MAIL_USERNAME` | *(optional)* Gmail address for contact email notifications | `you@gmail.com` |
| `MAIL_PASSWORD` | *(optional)* Gmail App Password (no spaces) | `abcdefghijklmnop` |
| `PORT` | Port (Render injects this automatically) | `8080` |

> `PORT` is already read from the environment in `application.properties` — Render sets it automatically, you don't need to add it manually.

4. Click **Deploy**. Render builds the Docker image and starts the container.  
   First deploy takes ~3–5 minutes (Maven downloads dependencies).

5. On first boot the app seeds default admin credentials and all resume content automatically.

### Default admin login

```
Username: admin
Password: Prince@Admin123
```

**Change this immediately** from Admin → Profile after first login, or set `ADMIN_USERNAME` / `ADMIN_PASSWORD` env vars before deploying.

---

## File Uploads on Render

> **Important:** Render's free/starter web services use an **ephemeral filesystem** — uploaded files (project images, certificates, resume PDFs, profile photo) are lost on every redeploy or restart.

To persist uploads, use one of:
- **Render Disk** (paid) — mount at `/app/uploads`, set env var `app.upload.dir=/app/uploads`
- **AWS S3 / Cloudflare R2** — requires code changes to swap the local file store for object storage

For a demo/portfolio site, the ephemeral filesystem is fine — just re-upload files after each deploy.

---

## Contact Form Email (Gmail)

1. Enable 2FA on your Google account.
2. Go to Google Account → Security → **App Passwords** → generate one for "Mail".
3. Copy the 16-character code (remove spaces) and set it as `MAIL_PASSWORD`.
4. Set `MAIL_USERNAME` to your Gmail address.
5. `app.mail.enabled=true` is already set in `application.properties`.

Contact messages are always saved to the database regardless of email config.

---

## GitHub / LeetCode Stats

Set your usernames in **Admin → About**:
- **GitHub Username** — calls `https://api.github.com/users/{username}` (no API key needed)
- **LeetCode Username** — calls `https://leetcode-stats-api.herokuapp.com/{username}` (public mirror)

If the server can't reach these URLs, the sections show a friendly fallback message.

---

## Environment Variables Reference (complete)

```env
# Required — already configured in Render
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/portfolio_db?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword
ADMIN_USERNAME=admin
ADMIN_PASSWORD=
ADMIN_EMAIL=you@example.com
JAVA_TOOL_OPTIONS=-Xmx256m

# Optional — only needed for contact form email notifications
MAIL_USERNAME=you@gmail.com
MAIL_PASSWORD=abcdefghijklmnop
```

---

## Project Structure

```
src/main/
├── java/com/example/portfolio/
│   ├── controller/          # MVC controllers (public + admin)
│   ├── model/               # JPA entities
│   ├── repository/          # Spring Data repositories
│   ├── service/             # Business logic
│   └── config/              # Security, MVC, seeder config
└── resources/
    ├── templates/           # Thymeleaf templates
    │   ├── index.html       # Public single-page portfolio
    │   └── admin/           # Admin panel pages
    ├── static/
    │   ├── css/theme.css    # Design tokens (colors, glassmorphism)
    │   ├── css/admin.css
    │   └── js/
    └── application.properties
Dockerfile
pom.xml
```

---

## Customization

- **Colors / theme:** edit CSS variables in `src/main/resources/static/css/theme.css` — one change applies everywhere.
- **Content:** all text, images, links, and resume data are editable from the admin panel at `/admin`.
- **Sort order:** items (skills, projects, etc.) use a numeric "Sort Order" field — no drag-and-drop.

---

## Build a JAR manually

```bash
./mvnw clean package -DskipTests
java -jar target/portfolio-0.0.1-SNAPSHOT.jar
```
