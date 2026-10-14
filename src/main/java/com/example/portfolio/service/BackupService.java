package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.Admin;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.Resume;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.entity.VisitorClassificationRecord;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ResumeRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SiteStatRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import com.example.portfolio.repository.VisitorClassificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final String CURRENT_VERSION = "2.1";
    private static final Set<String> SENSITIVE_ABOUT_FIELDS = Set.of(
            "whatsappNotificationApiKey",
            "whatsappNotificationWebhookUrl",
            "resendApiKey",
            "brevoApiKey",
            "geminiApiKey"
    );
    private static final Map<String, String> CATEGORY_NAMES = Map.ofEntries(
            Map.entry("aboutInfos", "Profile and site settings"),
            Map.entry("skills", "Skills"),
            Map.entry("projects", "Projects"),
            Map.entry("experiences", "Experience"),
            Map.entry("education", "Education"),
            Map.entry("certificates", "Certificates"),
            Map.entry("services", "Services"),
            Map.entry("testimonials", "Testimonials"),
            Map.entry("buildingProjects", "Building projects"),
            Map.entry("learningProjects", "Learning projects"),
            Map.entry("messages", "Inbound messages"),
            Map.entry("siteStats", "Site analytics"),
            Map.entry("visitorVisits", "Visitor classifications"),
            Map.entry("aiTrainingRules", "AI Training rules"),
            Map.entry("resumes", "Resume metadata"),
            Map.entry("files", "Uploaded files")
    );
    private static final Set<String> LEGACY_CATEGORIES = Set.of(
            "skills", "projects", "experiences", "education", "certificates", "services",
            "testimonials", "buildingProjects", "learningProjects", "messages", "siteStats"
    );
    private static final Set<String> LEGACY_ROOT_FIELDS = Set.of(
            "schemaVersion", "exportedAt", "app", "recordCounts", "aboutInfo",
            "skills", "projects", "experiences", "education", "certificates", "services",
            "testimonials", "buildingProjects", "learningProjects", "messages", "siteStats"
    );
    private static final Set<String> VERSION_2_CATEGORIES = Set.of(
            "aboutInfos", "skills", "projects", "experiences", "education", "certificates",
            "services", "testimonials", "buildingProjects", "learningProjects", "messages",
            "siteStats", "aiTrainingRules", "resumes", "files"
    );
    private static final Set<String> VERSION_2_ROOT_FIELDS = Set.of(
            "schemaVersion", "exportedAt", "app", "recordCounts", "excludedCategories",
            "aboutInfos", "skills", "projects", "experiences", "education", "certificates",
            "services", "testimonials", "buildingProjects", "learningProjects", "messages",
            "siteStats", "aiTrainingRules", "resumes", "files"
    );
    private static final Set<String> CURRENT_ROOT_FIELDS = Set.of(
            "schemaVersion", "exportedAt", "app", "recordCounts", "excludedCategories",
            "aboutInfos", "skills", "projects", "experiences", "education", "certificates",
            "services", "testimonials", "buildingProjects", "learningProjects", "messages",
            "siteStats", "visitorVisits", "aiTrainingRules", "resumes", "files"
    );

    private final AboutInfoRepository aboutInfoRepository;
    private final AdminRepository adminRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final CertificateRepository certificateRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final TestimonialRepository testimonialRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final SiteStatRepository siteStatRepository;
    private final VisitorClassificationRepository visitorClassificationRepository;
    private final AiTrainingRepository aiTrainingRepository;
    private final ResumeRepository resumeRepository;
    private final DataVersionService dataVersionService;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public BackupService(AboutInfoRepository aboutInfoRepository,
                         AdminRepository adminRepository,
                         SkillRepository skillRepository,
                         ProjectRepository projectRepository,
                         ExperienceRepository experienceRepository,
                         EducationEntryRepository educationEntryRepository,
                         CertificateRepository certificateRepository,
                         ServiceItemRepository serviceItemRepository,
                         TestimonialRepository testimonialRepository,
                         BuildingProjectRepository buildingProjectRepository,
                         LearningProjectRepository learningProjectRepository,
                         ContactMessageRepository contactMessageRepository,
                         SiteStatRepository siteStatRepository,
                         VisitorClassificationRepository visitorClassificationRepository,
                         AiTrainingRepository aiTrainingRepository,
                         ResumeRepository resumeRepository,
                         DataVersionService dataVersionService,
                         EntityManager entityManager,
                         JdbcTemplate jdbcTemplate) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.adminRepository = adminRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.certificateRepository = certificateRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.siteStatRepository = siteStatRepository;
        this.visitorClassificationRepository = visitorClassificationRepository;
        this.aiTrainingRepository = aiTrainingRepository;
        this.resumeRepository = resumeRepository;
        this.dataVersionService = dataVersionService;
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static class BackupPayload {
        public String schemaVersion = CURRENT_VERSION;
        public String exportedAt;
        public String app = "Prince Gupt Portfolio CMS";
        public Map<String, Long> recordCounts = new LinkedHashMap<>();
        public Map<String, String> excludedCategories = new LinkedHashMap<>();

        public List<AboutInfo> aboutInfos;
        public List<Skill> skills;
        public List<Project> projects;
        public List<Experience> experiences;
        public List<EducationEntry> education;
        public List<Certificate> certificates;
        public List<ServiceItem> services;
        public List<Testimonial> testimonials;
        public List<BuildingProject> buildingProjects;
        public List<LearningProject> learningProjects;
        public List<ContactMessage> messages;
        public List<SiteStat> siteStats;
        public List<VisitorClassificationRecord> visitorVisits;
        public List<AiTraining> aiTrainingRules;
        public List<Resume> resumes;
        public List<BackupFile> files;
    }

    public static class BackupFile {
        public String path;
        public String contentBase64;

        public BackupFile() {
        }

        BackupFile(String path, String contentBase64) {
            this.path = path;
            this.contentBase64 = contentBase64;
        }
    }

    public record RestoreResult(Map<String, Integer> restoredCounts,
                                Map<String, String> skippedCategories) {
        public int totalRestored() {
            return restoredCounts.values().stream().mapToInt(Integer::intValue).sum();
        }

        public String summary() {
            String restored = restoredCounts.entrySet().stream()
                    .map(entry -> CATEGORY_NAMES.getOrDefault(entry.getKey(), entry.getKey())
                            + ": " + entry.getValue())
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("no records");
            String skipped = skippedCategories.entrySet().stream()
                    .map(entry -> CATEGORY_NAMES.getOrDefault(entry.getKey(), entry.getKey())
                            + " (" + entry.getValue() + ")")
                    .reduce((left, right) -> left + "; " + right)
                    .orElse("none");
            return "Restore completed. Restored: " + restored + ". Skipped/preserved: " + skipped + ".";
        }
    }

    public Map<String, Long> getEntityCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("aboutInfos", aboutInfoRepository.count());
        counts.put("projects", projectRepository.count());
        counts.put("skills", skillRepository.count());
        counts.put("experiences", experienceRepository.count());
        counts.put("education", educationEntryRepository.count());
        counts.put("certificates", certificateRepository.count());
        counts.put("services", serviceItemRepository.count());
        counts.put("testimonials", testimonialRepository.count());
        counts.put("messages", contactMessageRepository.count());
        counts.put("buildingProjects", buildingProjectRepository.count());
        counts.put("learningProjects", learningProjectRepository.count());
        counts.put("siteStats", siteStatRepository.count());
        counts.put("visitorVisits", visitorClassificationRepository.count());
        counts.put("aiTrainingRules", aiTrainingRepository.count());
        counts.put("resumes", resumeRepository.count());
        counts.put("adminsExcluded", adminRepository.count());
        counts.put("files", countUploadedFiles());
        return counts;
    }

    @Transactional(readOnly = true)
    public byte[] exportBackupBytes() {
        try {
            BackupPayload payload = new BackupPayload();
            payload.exportedAt = Instant.now().toString();
            payload.excludedCategories.put("admins",
                    "Admin credentials are excluded; existing administrator accounts remain unchanged on restore.");
            payload.excludedCategories.put("sensitiveSettings",
                    "API keys and webhook credentials are excluded; restore them through secure admin settings or environment variables.");
            payload.excludedCategories.put("applicationAssets",
                    "Assets shipped with the application are part of the deployment, not the uploaded-file store.");
            payload.excludedCategories.put("externalFileContents",
                    "Remote files referenced by URL are retained as references only; their remote contents must be recovered from their original host.");

            payload.aboutInfos = aboutInfoRepository.findAll();
            payload.skills = skillRepository.findAll();
            payload.projects = projectRepository.findAll();
            payload.experiences = experienceRepository.findAll();
            payload.education = educationEntryRepository.findAll();
            payload.certificates = certificateRepository.findAll();
            payload.services = serviceItemRepository.findAll();
            payload.testimonials = testimonialRepository.findAll();
            payload.buildingProjects = buildingProjectRepository.findAll();
            payload.learningProjects = learningProjectRepository.findAll();
            payload.messages = contactMessageRepository.findAll();
            payload.siteStats = siteStatRepository.findAll();
            payload.visitorVisits = visitorClassificationRepository.findAll();
            payload.aiTrainingRules = aiTrainingRepository.findAll();
            payload.resumes = resumeRepository.findAll();
            payload.files = readUploadedFiles();
            validateUploadedFileReferences(payload);

            payload.recordCounts.put("aboutInfos", (long) payload.aboutInfos.size());
            payload.recordCounts.put("skills", (long) payload.skills.size());
            payload.recordCounts.put("projects", (long) payload.projects.size());
            payload.recordCounts.put("experiences", (long) payload.experiences.size());
            payload.recordCounts.put("education", (long) payload.education.size());
            payload.recordCounts.put("certificates", (long) payload.certificates.size());
            payload.recordCounts.put("services", (long) payload.services.size());
            payload.recordCounts.put("testimonials", (long) payload.testimonials.size());
            payload.recordCounts.put("buildingProjects", (long) payload.buildingProjects.size());
            payload.recordCounts.put("learningProjects", (long) payload.learningProjects.size());
            payload.recordCounts.put("messages", (long) payload.messages.size());
            payload.recordCounts.put("siteStats", (long) payload.siteStats.size());
            payload.recordCounts.put("visitorVisits", (long) payload.visitorVisits.size());
            payload.recordCounts.put("aiTrainingRules", (long) payload.aiTrainingRules.size());
            payload.recordCounts.put("resumes", (long) payload.resumes.size());
            payload.recordCounts.put("files", (long) payload.files.size());

            ObjectNode json = objectMapper.valueToTree(payload);
            redactSensitiveAboutFields(json.withArray("aboutInfos"));
            return objectMapper.writeValueAsBytes(json);
        } catch (Exception e) {
            log.error("Failed to generate complete database backup JSON: {}", e.getMessage(), e);
            throw new IllegalStateException("Export failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public synchronized RestoreResult restoreBackup(InputStream inputStream) {
        try {
            JsonNode root = objectMapper.readTree(inputStream);
            String version = validateBackup(root);
            ObjectNode sanitized = (ObjectNode) root.deepCopy();
            JsonNode aboutRows = aboutRowsForVersion(sanitized, version);
            if (aboutRows instanceof ArrayNode aboutArray) {
                redactSensitiveAboutFields(aboutArray);
            } else if (aboutRows instanceof ObjectNode aboutObject) {
                redactSensitiveAboutFields(List.of(aboutObject));
            }

            Map<String, Integer> restored = new LinkedHashMap<>();
            Map<String, String> skipped = new LinkedHashMap<>();
            skipped.put("admins", "credential-bearing admin accounts were intentionally not exported or modified");
            skipped.put("sensitiveSettings", "API keys/webhook credentials were not imported; existing values remain unchanged");
            skipped.put("applicationAssets", "static application assets are supplied by the deployment, not the database backup");
            skipped.put("externalFileContents", "remote files remain URL references and must be recovered from their original host");

            if (!isVersionOne(version)) {
                List<BackupFile> files = readUploadedFileRecords(sanitized.get("files"));
                FileRestoreResult fileRestore = restoreUploadedFiles(files);
                rewriteUploadReferences(sanitized, fileRestore.remappedPaths());
                ArrayNode fileRows = (ArrayNode) sanitized.get("files");
                for (int i = 0; i < files.size(); i++) {
                    ((ObjectNode) fileRows.get(i)).put("path", files.get(i).path);
                }
                int unlistedUploads = fileRestore.unlistedUploads();
                restored.put("files", files.size());
                if (unlistedUploads > 0) {
                    skipped.put("unlistedUploads", unlistedUploads + " existing uploaded file(s) not in the backup were left untouched");
                }
                if (!fileRestore.remappedPaths().isEmpty()) {
                    skipped.put("filePathConflicts",
                            fileRestore.remappedPaths().size() + " existing upload path(s) contained different data; restored copies use conflict-safe paths and backup references were updated");
                }
            } else {
                skipped.put("aiTrainingRules", "not present in the legacy export; existing rules were preserved");
                skipped.put("resumes", "not present in the legacy export; existing resume metadata was preserved");
                skipped.put("files", "legacy exports contain file references only, not uploaded file contents");
            }

            List<AboutInfo> aboutInfos = readAboutInfos(aboutRows, version);
            if (!isVersionOne(version)) {
                replaceCategory(aboutInfos, AboutInfo.class);
                restored.put("aboutInfos", aboutInfos.size());
            } else if (aboutRows instanceof ObjectNode) {
                saveLegacyAboutInfo(aboutInfos);
                restored.put("aboutInfos", aboutInfos.size());
            } else {
                skipped.put("aboutInfos", "category is absent from this legacy backup");
            }

            restoreList(sanitized, "skills", Skill.class, skillRepository, Skill::getId, version, restored, skipped);
            restoreList(sanitized, "projects", Project.class, projectRepository, Project::getId, version, restored, skipped);
            restoreList(sanitized, "experiences", Experience.class, experienceRepository, Experience::getId, version, restored, skipped);
            restoreList(sanitized, "education", EducationEntry.class, educationEntryRepository, EducationEntry::getId, version, restored, skipped);
            restoreList(sanitized, "certificates", Certificate.class, certificateRepository, Certificate::getId, version, restored, skipped);
            restoreList(sanitized, "services", ServiceItem.class, serviceItemRepository, ServiceItem::getId, version, restored, skipped);
            restoreList(sanitized, "testimonials", Testimonial.class, testimonialRepository, Testimonial::getId, version, restored, skipped);
            restoreList(sanitized, "buildingProjects", BuildingProject.class, buildingProjectRepository, BuildingProject::getId, version, restored, skipped);
            restoreList(sanitized, "learningProjects", LearningProject.class, learningProjectRepository, LearningProject::getId, version, restored, skipped);
            restoreList(sanitized, "messages", ContactMessage.class, contactMessageRepository, ContactMessage::getId, version, restored, skipped);
            restoreList(sanitized, "siteStats", SiteStat.class, siteStatRepository, SiteStat::getId, version, restored, skipped);
            if (CURRENT_VERSION.equals(version)) {
                restoreList(sanitized, "visitorVisits", VisitorClassificationRecord.class,
                        visitorClassificationRepository, VisitorClassificationRecord::getId,
                        version, restored, skipped);
            } else {
                skipped.put("visitorVisits", "not present in this backup; current visitor classifications were preserved");
            }

            if (!isVersionOne(version)) {
                restoreList(sanitized, "aiTrainingRules", AiTraining.class, aiTrainingRepository, AiTraining::getId, version, restored, skipped);
                restoreList(sanitized, "resumes", Resume.class, resumeRepository, Resume::getId, version, restored, skipped);
            }

            entityManager.flush();
            verifyRestoredCounts(sanitized, version);
            dataVersionService.bump();
            return new RestoreResult(Map.copyOf(restored), Map.copyOf(skipped));
        } catch (Exception e) {
            log.error("Database restore FAILED and was rolled back: {}", e.getMessage(), e);
            throw new IllegalStateException("Restore failed; no database changes were committed: " + e.getMessage(), e);
        }
    }

    private String validateBackup(JsonNode root) throws JsonProcessingException {
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Backup must be a JSON object.");
        }
        JsonNode versionNode = root.get("schemaVersion");
        if (versionNode == null || !versionNode.isTextual()) {
            throw new IllegalArgumentException("Backup schemaVersion is missing or invalid.");
        }
        String version = versionNode.asText();
        Set<String> rootFields;
        if ("1.0".equals(version)) {
            rootFields = LEGACY_ROOT_FIELDS;
        } else if ("2.0".equals(version)) {
            rootFields = VERSION_2_ROOT_FIELDS;
        } else if (CURRENT_VERSION.equals(version)) {
            rootFields = CURRENT_ROOT_FIELDS;
        } else {
            throw new IllegalArgumentException("Unsupported backup schema version: " + version);
        }
        root.fieldNames().forEachRemaining(field -> {
            if (!rootFields.contains(field)) {
                throw new IllegalArgumentException("Unsupported backup field: " + field);
            }
        });

        if (!isVersionOne(version)) {
            JsonNode app = root.get("app");
            if (app == null || !app.isTextual() || app.asText().isBlank()) {
                throw new IllegalArgumentException("Backup application metadata is missing or invalid.");
            }
            if (root.get("exportedAt") == null || !root.get("exportedAt").isTextual()) {
                throw new IllegalArgumentException("Backup export timestamp is missing or invalid.");
            }
            try {
                Instant.parse(root.get("exportedAt").asText());
            } catch (Exception e) {
                throw new IllegalArgumentException("Backup export timestamp is not a valid ISO-8601 instant.", e);
            }
            if (root.get("recordCounts") == null || !root.get("recordCounts").isObject()) {
                throw new IllegalArgumentException("Backup recordCounts is missing or invalid.");
            }
            Set<String> requiredCategories = CURRENT_VERSION.equals(version)
                    ? CATEGORY_NAMES.keySet() : VERSION_2_CATEGORIES;
            for (String category : requiredCategories) {
                JsonNode rows = root.get(category);
                if (rows == null || !rows.isArray()) {
                    throw new IllegalArgumentException("Backup category '" + category + "' is missing or invalid.");
                }
            }
            JsonNode exclusions = root.get("excludedCategories");
            if (exclusions == null || !exclusions.isObject()) {
                throw new IllegalArgumentException("Backup excludedCategories metadata is missing or invalid.");
            }
            for (String requiredExclusion : List.of("admins", "sensitiveSettings", "applicationAssets", "externalFileContents")) {
                if (!exclusions.has(requiredExclusion)) {
                    throw new IllegalArgumentException("Backup exclusion metadata is missing '" + requiredExclusion + "'.");
                }
            }
            var excludedFields = exclusions.fieldNames();
            while (excludedFields.hasNext()) {
                String category = excludedFields.next();
                JsonNode reason = exclusions.get(category);
                if (!reason.isTextual() || reason.asText().isBlank()) {
                    throw new IllegalArgumentException("Backup exclusion metadata is invalid for '" + category + "'.");
                }
            }
            validateRecordCounts(root, requiredCategories);
        } else {
            validateLegacyRecordCounts(root);
        }

        validateEntityRows(root, version);
        validateBackupFiles(root, version);
        validateBackupUploadedFileReferences(root, version);
        return version;
    }

    private void validateRecordCounts(JsonNode root, Set<String> categories) {
        JsonNode counts = root.get("recordCounts");
        if (counts.size() != categories.size()) {
            throw new IllegalArgumentException("Backup recordCounts must describe every category.");
        }
        for (String category : categories) {
            JsonNode count = counts.get(category);
            if (count == null || !count.isIntegralNumber() || count.longValue() < 0
                    || count.longValue() != root.get(category).size()) {
                throw new IllegalArgumentException("Backup count does not match category '" + category + "'.");
            }
        }
    }

    private void validateLegacyRecordCounts(JsonNode root) {
        JsonNode counts = root.get("recordCounts");
        if (counts == null || counts.isNull()) return;
        if (!counts.isObject()) throw new IllegalArgumentException("Legacy backup recordCounts is invalid.");
        counts.fieldNames().forEachRemaining(field -> {
            if (!LEGACY_CATEGORIES.contains(field)) {
                throw new IllegalArgumentException("Unsupported legacy count category: " + field);
            }
            JsonNode rows = root.get(field);
            JsonNode count = counts.get(field);
            if (count == null || !count.isIntegralNumber() || count.longValue() < 0) {
                throw new IllegalArgumentException("Legacy backup count is invalid for category '" + field + "'.");
            }
            if (rows != null && rows.isArray() && count.longValue() != rows.size()) {
                throw new IllegalArgumentException("Legacy backup count does not match category '" + field + "'.");
            }
        });
    }

    private void validateEntityRows(JsonNode root, String version) throws JsonProcessingException {
        JsonNode aboutRows = aboutRowsForVersion(root, version);
        validateRows(aboutRows, AboutInfo.class, "aboutInfos", version, new String[0]);
        validateRows(root.get("skills"), Skill.class, "skills", version, "name", "category", "proficiency");
        validateRows(root.get("projects"), Project.class, "projects", version, "title");
        validateRows(root.get("experiences"), Experience.class, "experiences", version, "company", "role");
        validateRows(root.get("education"), EducationEntry.class, "education", version, new String[0]);
        validateRows(root.get("certificates"), Certificate.class, "certificates", version, "title");
        validateRows(root.get("services"), ServiceItem.class, "services", version, "title");
        validateRows(root.get("testimonials"), Testimonial.class, "testimonials", version, "name", "rating");
        validateRows(root.get("buildingProjects"), BuildingProject.class, "buildingProjects", version, new String[0]);
        validateRows(root.get("learningProjects"), LearningProject.class, "learningProjects", version, new String[0]);
        validateRows(root.get("messages"), ContactMessage.class, "messages", version, "name", "email", "message");
        validateRows(root.get("siteStats"), SiteStat.class, "siteStats", version, "statDate");
        validateUniqueValues(root.get("siteStats"), "statDate", "siteStats");
        if (!isVersionOne(version)) {
            validateRows(root.get("aiTrainingRules"), AiTraining.class, "aiTrainingRules", version, "questionTrigger", "correctAnswer");
            validateRows(root.get("resumes"), Resume.class, "resumes", version, "fileUrl", "versionLabel");
        }
        if (CURRENT_VERSION.equals(version)) {
            validateRows(root.get("visitorVisits"), VisitorClassificationRecord.class,
                    "visitorVisits", version, "visitDate", "visitorKeyHash", "classification");
            validateUniqueVisitorDates(root.get("visitorVisits"));
        }
    }

    private void validateUniqueVisitorDates(JsonNode rows) {
        if (rows == null || !rows.isArray()) return;
        Set<String> keys = new HashSet<>();
        for (JsonNode row : rows) {
            String key = row.path("visitDate").asText() + "\n" + row.path("visitorKeyHash").asText();
            if (!row.path("visitorKeyHash").asText().matches("[0-9a-f]{64}")) {
                throw new IllegalArgumentException("Backup category 'visitorVisits' contains an invalid visitor key.");
            }
            if (!keys.add(key)) {
                throw new IllegalArgumentException("Backup category 'visitorVisits' contains duplicate visitor/date records.");
            }
        }
    }

    private void validateUniqueValues(JsonNode rows, String field, String category) {
        if (rows == null || !rows.isArray()) return;
        Set<String> values = new HashSet<>();
        for (JsonNode row : rows) {
            JsonNode value = row.get(field);
            if (value != null && !value.isNull() && !values.add(value.asText())) {
                throw new IllegalArgumentException("Backup category '" + category + "' contains duplicate " + field + " values.");
            }
        }
    }

    private void validateRows(JsonNode rows, Class<?> type, String category, String version,
                              String... requiredFields) throws JsonProcessingException {
        if (rows == null || rows.isNull()) return;
        if ("aboutInfos".equals(category) && "1.0".equals(version) && rows.isObject()) {
            validateRow(rows, type, category, version, requiredFields);
            return;
        }
        if (!rows.isArray()) throw new IllegalArgumentException("Backup category '" + category + "' must be an array.");
        Set<Long> ids = new HashSet<>();
        for (JsonNode row : rows) {
            validateRow(row, type, category, version, requiredFields);
            JsonNode id = row.get("id");
            if (id != null && !id.isNull()) {
                if (!id.isIntegralNumber() || id.longValue() <= 0 || !ids.add(id.longValue())) {
                    throw new IllegalArgumentException("Backup category '" + category + "' has an invalid or duplicate ID.");
                }
            } else if (!isVersionOne(version)) {
                throw new IllegalArgumentException("Backup category '" + category + "' contains a record without an ID.");
            }
        }
    }

    private void validateRow(JsonNode row, Class<?> type, String category, String version,
                             String... requiredFields) throws JsonProcessingException {
        if (!row.isObject()) throw new IllegalArgumentException("Backup category '" + category + "' contains a non-object record.");
        for (String field : requiredFields) {
            JsonNode value = row.get(field);
            if (value == null || value.isNull() || (value.isTextual() && value.asText().isBlank())) {
                throw new IllegalArgumentException("Backup category '" + category + "' has a record missing required field '" + field + "'.");
            }
            if (!"proficiency".equals(field) && !"rating".equals(field) && !"statDate".equals(field)
                    && !value.isTextual()) {
                throw new IllegalArgumentException("Backup category '" + category + "' has an invalid field '" + field + "'.");
            }
        }
        validateKnownEntityProperties(row, type, category);
        if ("skills".equals(category) && !row.get("proficiency").canConvertToInt()) {
            throw new IllegalArgumentException("Backup skill proficiency must be an integer.");
        }
        if ("testimonials".equals(category) && !row.get("rating").canConvertToInt()) {
            throw new IllegalArgumentException("Backup testimonial rating must be an integer.");
        }
        objectMapper.treeToValue(row, type);
    }

    private void validateBackupFiles(JsonNode root, String version) {
        if (isVersionOne(version)) return;
        JsonNode files = root.get("files");
        Set<String> paths = new HashSet<>();
        for (JsonNode file : files) {
            if (!file.isObject() || file.get("path") == null || !file.get("path").isTextual()
                    || file.get("contentBase64") == null || !file.get("contentBase64").isTextual()) {
                throw new IllegalArgumentException("Uploaded file entry is invalid.");
            }
            file.fieldNames().forEachRemaining(field -> {
                if (!Set.of("path", "contentBase64").contains(field)) {
                    throw new IllegalArgumentException("Unsupported uploaded file field: " + field);
                }
            });
            String relativePath = file.get("path").asText();
            validateUploadPath(relativePath);
            if (!paths.add(relativePath)) throw new IllegalArgumentException("Duplicate uploaded file path: " + relativePath);
            try {
                Base64.getDecoder().decode(file.get("contentBase64").asText());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Uploaded file content is invalid for: " + relativePath, e);
            }
        }
    }

    private void validateKnownEntityProperties(JsonNode row, Class<?> type, String category) {
        BeanDescription description = objectMapper.getDeserializationConfig().introspect(objectMapper.constructType(type));
        Set<String> properties = new HashSet<>();
        for (BeanPropertyDefinition property : description.findProperties()) {
            if (property.couldDeserialize()) properties.add(property.getName());
        }
        if (EducationEntry.class.equals(type)) properties.addAll(Set.of("startYear", "endYear"));
        if (Experience.class.equals(type)) properties.addAll(Set.of("startDate", "endDate"));
        row.fieldNames().forEachRemaining(field -> {
            if (!properties.contains(field)) {
                throw new IllegalArgumentException("Unsupported field '" + field + "' in backup category '" + category + "'.");
            }
        });
    }

    private void validateBackupUploadedFileReferences(JsonNode root, String version) {
        if (isVersionOne(version)) return;
        Set<String> filePaths = new HashSet<>();
        for (JsonNode file : root.get("files")) filePaths.add(file.get("path").asText());
        validateFileReferences(root.get("aboutInfos"), filePaths, "profileImage", "resumeUrl", "ogImageUrl");
        validateFileReferences(root.get("projects"), filePaths, "imageUrl", "architectureImageUrl");
        validateFileReferences(root.get("certificates"), filePaths, "imageUrl", "pdfUrl");
        validateFileReferences(root.get("testimonials"), filePaths, "photoUrl");
        validateFileReferences(root.get("resumes"), filePaths, "fileUrl");
    }

    private void validateFileReferences(JsonNode records, Set<String> filePaths, String... fields) {
        for (JsonNode record : records) {
            for (String field : fields) {
                JsonNode value = record.get(field);
                if (value == null || value.isNull()) continue;
                if (!value.isTextual()) {
                    throw new IllegalArgumentException("Uploaded file reference '" + field + "' must be a string.");
                }
                String reference = value.asText();
                if (!reference.startsWith("/uploads/")) continue;
                String relativePath = reference.substring("/uploads/".length());
                validateUploadPath(relativePath);
                if (!filePaths.contains(relativePath)) {
                    throw new IllegalArgumentException("Backup is missing uploaded file contents for reference '" + reference + "'.");
                }
            }
        }
    }

    private JsonNode aboutRowsForVersion(JsonNode root, String version) {
        if (!isVersionOne(version)) return root.get("aboutInfos");
        return root.get("aboutInfo");
    }

    private List<AboutInfo> readAboutInfos(JsonNode rows, String version) throws IOException {
        if (rows == null || rows.isNull()) return List.of();
        if ("1.0".equals(version)) {
            AboutInfo incoming = readRecords(arrayOf(rows), AboutInfo.class, aboutInfoRepository, AboutInfo::getId, true).get(0);
            return List.of(incoming);
        }
        List<AboutInfo> incoming = readRecords(rows, AboutInfo.class, aboutInfoRepository, AboutInfo::getId, false);
        Map<Long, AboutInfo> currentById = new LinkedHashMap<>();
        aboutInfoRepository.findAll().forEach(info -> currentById.put(info.getId(), info));
        AboutInfo currentDefault = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
        for (AboutInfo info : incoming) {
            AboutInfo current = info.getId() == null ? currentDefault : currentById.get(info.getId());
            copySensitiveSettings(current, info);
        }
        return incoming;
    }

    private JsonNode arrayOf(JsonNode object) {
        ArrayNode array = objectMapper.createArrayNode();
        array.add(object);
        return array;
    }

    private void saveLegacyAboutInfo(List<AboutInfo> records) {
        replaceCategory(records, AboutInfo.class);
    }

    private <T> void restoreList(ObjectNode root, String category, Class<T> type,
                                 JpaRepository<T, Long> repository, Function<T, Long> idGetter,
                                 String version, Map<String, Integer> restored,
                                 Map<String, String> skipped) throws Exception {
        JsonNode rows = root.get(category);
        if (rows == null || rows.isNull()) {
            skipped.put(category, "category is absent from this backup; current records were preserved");
            return;
        }
        List<T> records = readRecords(rows, type, repository, idGetter, "1.0".equals(version));
        if ("1.0".equals(version) && records.isEmpty()) {
            skipped.put(category, "legacy empty categories are not treated as authoritative; current records were preserved");
            return;
        }
        replaceCategory(records, type);
        restored.put(category, records.size());
    }

    private <T> List<T> readRecords(JsonNode rows, Class<T> type, JpaRepository<T, Long> repository,
                                   Function<T, Long> idGetter, boolean partialLegacy) throws IOException {
        if (rows == null || rows.isNull()) return List.of();
        if (!rows.isArray()) throw new IllegalArgumentException("Expected a JSON array for " + type.getSimpleName() + ".");
        List<T> records = new ArrayList<>();
        for (JsonNode row : rows) {
            T record = objectMapper.treeToValue(row, type);
            Long id = idGetter.apply(record);
            if (partialLegacy && repository != null && id != null) {
                T existing = repository.findById(id).orElse(null);
                if (existing != null) {
                    ObjectNode merged = objectMapper.valueToTree(existing);
                    merged.setAll((ObjectNode) row);
                    record = objectMapper.treeToValue(merged, type);
                }
            }
            records.add(record);
        }
        return records;
    }

    private List<BackupFile> readUploadedFileRecords(JsonNode rows) throws JsonProcessingException {
        List<BackupFile> records = new ArrayList<>();
        for (JsonNode row : rows) records.add(objectMapper.treeToValue(row, BackupFile.class));
        return records;
    }

    private <T> void replaceCategory(List<T> records, Class<T> type) {
        Table table = type.getAnnotation(Table.class);
        if (table == null || table.name().isBlank()) {
            throw new IllegalStateException("No explicit backup table mapping is configured for " + type.getSimpleName() + ".");
        }
        List<Field> fields = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || field.isAnnotationPresent(Transient.class)) continue;
            fields.add(field);
        }
        if (fields.stream().noneMatch(field -> field.isAnnotationPresent(Id.class))) {
            throw new IllegalStateException("No primary key is mapped for " + type.getSimpleName() + ".");
        }

        entityManager.clear();
        jdbcTemplate.update("DELETE FROM " + table.name());
        if (records.isEmpty()) return;

        String columns = fields.stream().map(BackupService::databaseColumnName)
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String placeholders = String.join(", ", java.util.Collections.nCopies(fields.size(), "?"));
        String insertSql = "INSERT INTO " + table.name() + " (" + columns + ") VALUES (" + placeholders + ")";
        jdbcTemplate.batchUpdate(insertSql, records, 100, (statement, record) -> {
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                try {
                    field.setAccessible(true);
                    Object value = field.get(record);
                    if (value instanceof Enum<?> enumValue) {
                        statement.setString(i + 1, enumValue.name());
                    } else {
                        statement.setObject(i + 1, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not read backup value " + type.getSimpleName() + "." + field.getName(), e);
                }
            }
        });
    }

    private static String databaseColumnName(Field field) {
        jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);
        String name = column != null && !column.name().isBlank()
                ? column.name()
                : field.getName().replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(java.util.Locale.ROOT);
        return name;
    }

    private void copySensitiveSettings(AboutInfo existing, AboutInfo incoming) {
        if (existing == null) return;
        incoming.setWhatsappNotificationApiKey(existing.getWhatsappNotificationApiKey());
        incoming.setWhatsappNotificationWebhookUrl(existing.getWhatsappNotificationWebhookUrl());
        incoming.setResendApiKey(existing.getResendApiKey());
        incoming.setBrevoApiKey(existing.getBrevoApiKey());
        incoming.setGeminiApiKey(existing.getGeminiApiKey());
    }

    private void redactSensitiveAboutFields(ArrayNode rows) {
        for (JsonNode row : rows) {
            if (row instanceof ObjectNode object) redactSensitiveAboutFields(List.of(object));
        }
    }

    private void redactSensitiveAboutFields(List<ObjectNode> rows) {
        for (ObjectNode row : rows) {
            SENSITIVE_ABOUT_FIELDS.forEach(row::remove);
        }
    }

    private void verifyRestoredCounts(ObjectNode root, String version) {
        if (isVersionOne(version)) return;
        Map<String, Long> actualCounts = getDatabaseRecordCounts();
        JsonNode expected = root.get("recordCounts");
        Set<String> categories = CURRENT_VERSION.equals(version)
                ? CATEGORY_NAMES.keySet() : VERSION_2_CATEGORIES;
        for (String category : categories) {
            if ("files".equals(category)) continue;
            if (actualCounts.get(category) == null || actualCounts.get(category) != expected.get(category).longValue()) {
                throw new IllegalStateException("Restored count mismatch for '" + category
                        + "': expected " + expected.get(category).longValue()
                        + ", found " + actualCounts.get(category) + ".");
            }
        }
    }

    private Map<String, Long> getDatabaseRecordCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("aboutInfos", aboutInfoRepository.count());
        counts.put("skills", skillRepository.count());
        counts.put("projects", projectRepository.count());
        counts.put("experiences", experienceRepository.count());
        counts.put("education", educationEntryRepository.count());
        counts.put("certificates", certificateRepository.count());
        counts.put("services", serviceItemRepository.count());
        counts.put("testimonials", testimonialRepository.count());
        counts.put("buildingProjects", buildingProjectRepository.count());
        counts.put("learningProjects", learningProjectRepository.count());
        counts.put("messages", contactMessageRepository.count());
        counts.put("siteStats", siteStatRepository.count());
        counts.put("visitorVisits", visitorClassificationRepository.count());
        counts.put("aiTrainingRules", aiTrainingRepository.count());
        counts.put("resumes", resumeRepository.count());
        return counts;
    }

    private boolean isVersionOne(String version) {
        return "1.0".equals(version);
    }

    private List<BackupFile> readUploadedFiles() throws IOException {
        Path root = uploadRoot();
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return List.of();
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Configured upload directory is not a safe directory.");
        }
        List<BackupFile> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path file : stream.filter(path -> !path.equals(root))
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> !isRestoreTemporaryFile(path))
                    .toList()) {
                if (Files.isSymbolicLink(file)) continue;
                String relative = root.relativize(file).toString().replace('\\', '/');
                files.add(new BackupFile(relative, Base64.getEncoder().encodeToString(Files.readAllBytes(file))));
            }
        }
        return files;
    }

    private void validateUploadedFileReferences(BackupPayload payload) throws IOException {
        Set<String> backedUpPaths = payload.files.stream().map(file -> file.path).collect(java.util.stream.Collectors.toSet());
        List<String> references = new ArrayList<>();
        for (AboutInfo info : payload.aboutInfos) {
            references.add(info.getProfileImage());
            references.add(info.getResumeUrl());
            references.add(info.getOgImageUrl());
        }
        for (Project project : payload.projects) {
            references.add(project.getImageUrl());
            references.add(project.getArchitectureImageUrl());
        }
        for (Certificate certificate : payload.certificates) {
            references.add(certificate.getImageUrl());
            references.add(certificate.getPdfUrl());
        }
        for (Testimonial testimonial : payload.testimonials) references.add(testimonial.getPhotoUrl());
        for (Resume resume : payload.resumes) references.add(resume.getFileUrl());
        for (Admin admin : adminRepository.findAll()) references.add(admin.getProfileImage());

        for (String reference : references) {
            if (reference == null || !reference.startsWith("/uploads/")) continue;
            String relativePath = reference.substring("/uploads/".length());
            validateUploadPath(relativePath);
            if (!backedUpPaths.contains(relativePath)) {
                throw new IOException("A database record references an uploaded file that is missing from storage: " + reference);
            }
        }
    }

    private FileRestoreResult restoreUploadedFiles(List<BackupFile> files) throws IOException {
        Path root = uploadRoot();
        Set<String> pathsInBackup = files.stream().map(file -> file.path).collect(java.util.stream.Collectors.toSet());
        Set<String> existingPaths = listUploadedPaths(root);
        existingPaths.removeAll(pathsInBackup);
        Files.createDirectories(root);
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Configured upload directory is not a safe directory.");
        }
        Set<Path> newlyCreatedFiles = new HashSet<>();
        Map<String, String> remappedPaths = new LinkedHashMap<>();
        try {
            for (BackupFile file : files) {
                String originalPath = file.path;
                Path target = resolveUploadPath(root, file.path);
                ensureNoSymlinkParents(root, target.getParent());
                Files.createDirectories(target.getParent());
                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)
                        && (Files.isSymbolicLink(target) || !Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS))) {
                    throw new IOException("Upload restore target is not a regular file: " + file.path);
                }

                byte[] contents = Base64.getDecoder().decode(file.contentBase64);
                Path temporaryFile = Files.createTempFile(target.getParent(), ".restore-", ".tmp");
                try {
                    Files.write(temporaryFile, contents);
                    if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                        if (Files.mismatch(temporaryFile, target) == -1) {
                            continue;
                        }
                        String restoredPath = "restored/" + contentHash(contents) + "/" + originalPath;
                        Path conflictSafeTarget = resolveUploadPath(root, restoredPath);
                        ensureNoSymlinkParents(root, conflictSafeTarget.getParent());
                        Files.createDirectories(conflictSafeTarget.getParent());
                        if (Files.exists(conflictSafeTarget, LinkOption.NOFOLLOW_LINKS)) {
                            if (Files.isSymbolicLink(conflictSafeTarget)
                                    || !Files.isRegularFile(conflictSafeTarget, LinkOption.NOFOLLOW_LINKS)
                                    || Files.mismatch(temporaryFile, conflictSafeTarget) != -1) {
                                throw new IOException("Conflict-safe upload restore target is not available: " + restoredPath);
                            }
                        } else {
                            moveRestoreFile(temporaryFile, conflictSafeTarget);
                            newlyCreatedFiles.add(conflictSafeTarget);
                        }
                        remappedPaths.put(originalPath, restoredPath);
                        file.path = restoredPath;
                    } else {
                        moveRestoreFile(temporaryFile, target);
                        newlyCreatedFiles.add(target);
                    }
                } catch (Exception e) {
                    Files.deleteIfExists(temporaryFile);
                    throw e;
                } finally {
                    Files.deleteIfExists(temporaryFile);
                }
            }
        } catch (Exception e) {
            rollbackUploadedFiles(newlyCreatedFiles, e);
            throw e;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        rollbackUploadedFiles(newlyCreatedFiles, null);
                    }
                }
            });
        }
        return new FileRestoreResult(remappedPaths, existingPaths.size());
    }

    private void moveRestoreFile(Path temporaryFile, Path target) throws IOException {
        Files.move(temporaryFile, target);
    }

    private void rollbackUploadedFiles(Set<Path> newlyCreatedFiles, Exception original) {
        try {
            for (Path path : newlyCreatedFiles) Files.deleteIfExists(path);
        } catch (IOException rollbackException) {
            if (original != null) original.addSuppressed(rollbackException);
            log.error("Failed to roll back uploaded files after database restore failure.", rollbackException);
        }
    }

    private long countUploadedFiles() {
        Path root = uploadRoot();
        try {
            if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return 0;
            if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("Configured upload directory is not a safe directory.");
            }
            try (Stream<Path> stream = Files.walk(root)) {
                return stream.filter(path -> !path.equals(root))
                        .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                        .filter(path -> !Files.isSymbolicLink(path))
                        .filter(path -> !isRestoreTemporaryFile(path))
                        .filter(path -> !isRestoreTemporaryFile(path))
                        .count();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not inspect uploaded files for the backup catalog.", e);
        }
    }

    private boolean isRestoreTemporaryFile(Path path) {
        return path.getFileName().toString().startsWith(".restore-");
    }

    private String contentHash(byte[] contents) throws IOException {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contents));
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Could not create a conflict-safe upload path.", e);
        }
    }

    private void rewriteUploadReferences(JsonNode node, Map<String, String> remappedPaths) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object.fieldNames().forEachRemaining(field -> {
                JsonNode value = object.get(field);
                if (value.isTextual() && value.asText().startsWith("/uploads/")) {
                    String relativePath = value.asText().substring("/uploads/".length());
                    String remapped = remappedPaths.get(relativePath);
                    if (remapped != null) object.put(field, "/uploads/" + remapped);
                } else {
                    rewriteUploadReferences(value, remappedPaths);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode item : node) rewriteUploadReferences(item, remappedPaths);
        }
    }

    private record FileRestoreResult(Map<String, String> remappedPaths, int unlistedUploads) {
    }

    private Set<String> listUploadedPaths(Path root) throws IOException {
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return new HashSet<>();
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Configured upload directory is not a safe directory.");
        }
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(path -> !path.equals(root))
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> !Files.isSymbolicLink(path))
                    .map(path -> root.relativize(path).toString().replace('\\', '/'))
                    .collect(java.util.stream.Collectors.toSet());
        }
    }

    private Path uploadRoot() {
        return Path.of(uploadDir).toAbsolutePath().normalize();
    }

    private Path resolveUploadPath(Path root, String relativePath) {
        validateUploadPath(relativePath);
        Path resolved = root.resolve(relativePath.replace('/', java.io.File.separatorChar)).normalize();
        if (!resolved.startsWith(root) || resolved.equals(root)) {
            throw new IllegalArgumentException("Unsafe uploaded file path: " + relativePath);
        }
        return resolved;
    }

    private void validateUploadPath(String relativePath) {
        if (relativePath == null || relativePath.isBlank() || relativePath.contains("\\")
                || relativePath.startsWith("/") || relativePath.matches("^[A-Za-z]:.*")) {
            throw new IllegalArgumentException("Unsafe uploaded file path in backup.");
        }
        Path path = Path.of(relativePath);
        if (path.isAbsolute()) throw new IllegalArgumentException("Absolute uploaded file paths are not allowed.");
        for (Path segment : path) {
            if ("..".equals(segment.toString()) || ".".equals(segment.toString())) {
                throw new IllegalArgumentException("Unsafe uploaded file path in backup.");
            }
        }
    }

    private void ensureNoSymlinkParents(Path root, Path parent) throws IOException {
        Path current = root;
        for (Path segment : root.relativize(parent)) {
            current = current.resolve(segment);
            if (Files.isSymbolicLink(current)) {
                throw new IOException("Upload restore path traverses a symbolic link.");
            }
        }
    }
}
