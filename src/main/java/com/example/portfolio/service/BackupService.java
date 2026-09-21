package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SiteStatRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    private final AboutInfoRepository aboutInfoRepository;
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
    private final DataVersionService dataVersionService;
    private final ObjectMapper objectMapper;

    public BackupService(AboutInfoRepository aboutInfoRepository,
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
                         DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
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
        this.dataVersionService = dataVersionService;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static class BackupPayload {
        public String schemaVersion = "1.0";
        public String exportedAt;
        public String app = "Prince Gupt Portfolio CMS";
        public Map<String, Integer> recordCounts = new HashMap<>();

        public AboutInfo aboutInfo;
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
    }

    public Map<String, Long> getEntityCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("projects", projectRepository.count());
        counts.put("skills", skillRepository.count());
        counts.put("experiences", experienceRepository.count());
        counts.put("certificates", certificateRepository.count());
        counts.put("education", educationEntryRepository.count());
        counts.put("services", serviceItemRepository.count());
        counts.put("testimonials", testimonialRepository.count());
        counts.put("messages", contactMessageRepository.count());
        counts.put("buildingProjects", buildingProjectRepository.count());
        counts.put("learningProjects", learningProjectRepository.count());
        return counts;
    }

    public byte[] exportBackupBytes() {
        try {
            BackupPayload payload = new BackupPayload();
            payload.exportedAt = Instant.now().toString();

            payload.aboutInfo = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
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

            payload.recordCounts.put("skills", payload.skills.size());
            payload.recordCounts.put("projects", payload.projects.size());
            payload.recordCounts.put("experiences", payload.experiences.size());
            payload.recordCounts.put("education", payload.education.size());
            payload.recordCounts.put("certificates", payload.certificates.size());
            payload.recordCounts.put("services", payload.services.size());
            payload.recordCounts.put("testimonials", payload.testimonials.size());
            payload.recordCounts.put("buildingProjects", payload.buildingProjects.size());
            payload.recordCounts.put("learningProjects", payload.learningProjects.size());
            payload.recordCounts.put("messages", payload.messages.size());

            return objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to generate database backup JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public int restoreBackup(InputStream inputStream) {
        try {
            BackupPayload payload = objectMapper.readValue(inputStream, BackupPayload.class);
            if (payload == null) {
                throw new IllegalArgumentException("Backup payload is empty or invalid JSON.");
            }

            int restoredCount = 0;

            // 1. AboutInfo (Upsert singleton)
            if (payload.aboutInfo != null) {
                AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
                if (existing != null) {
                    payload.aboutInfo.setId(existing.getId());
                } else {
                    payload.aboutInfo.setId(null);
                }
                aboutInfoRepository.save(payload.aboutInfo);
                restoredCount++;
            }

            // 2. Skills
            if (payload.skills != null && !payload.skills.isEmpty()) {
                skillRepository.deleteAll();
                payload.skills.forEach(s -> s.setId(null));
                skillRepository.saveAll(payload.skills);
                restoredCount += payload.skills.size();
            }

            // 3. Projects
            if (payload.projects != null && !payload.projects.isEmpty()) {
                projectRepository.deleteAll();
                payload.projects.forEach(p -> p.setId(null));
                projectRepository.saveAll(payload.projects);
                restoredCount += payload.projects.size();
            }

            // 4. Experience
            if (payload.experiences != null && !payload.experiences.isEmpty()) {
                experienceRepository.deleteAll();
                payload.experiences.forEach(e -> e.setId(null));
                experienceRepository.saveAll(payload.experiences);
                restoredCount += payload.experiences.size();
            }

            // 5. Education
            if (payload.education != null && !payload.education.isEmpty()) {
                educationEntryRepository.deleteAll();
                payload.education.forEach(ed -> ed.setId(null));
                educationEntryRepository.saveAll(payload.education);
                restoredCount += payload.education.size();
            }

            // 6. Certificates
            if (payload.certificates != null && !payload.certificates.isEmpty()) {
                certificateRepository.deleteAll();
                payload.certificates.forEach(c -> c.setId(null));
                certificateRepository.saveAll(payload.certificates);
                restoredCount += payload.certificates.size();
            }

            // 7. Services
            if (payload.services != null && !payload.services.isEmpty()) {
                serviceItemRepository.deleteAll();
                payload.services.forEach(s -> s.setId(null));
                serviceItemRepository.saveAll(payload.services);
                restoredCount += payload.services.size();
            }

            // 8. Testimonials
            if (payload.testimonials != null && !payload.testimonials.isEmpty()) {
                testimonialRepository.deleteAll();
                payload.testimonials.forEach(t -> t.setId(null));
                testimonialRepository.saveAll(payload.testimonials);
                restoredCount += payload.testimonials.size();
            }

            // 9. Building Projects
            if (payload.buildingProjects != null && !payload.buildingProjects.isEmpty()) {
                buildingProjectRepository.deleteAll();
                payload.buildingProjects.forEach(b -> b.setId(null));
                buildingProjectRepository.saveAll(payload.buildingProjects);
                restoredCount += payload.buildingProjects.size();
            }

            // 10. Learning Projects
            if (payload.learningProjects != null && !payload.learningProjects.isEmpty()) {
                learningProjectRepository.deleteAll();
                payload.learningProjects.forEach(l -> l.setId(null));
                learningProjectRepository.saveAll(payload.learningProjects);
                restoredCount += payload.learningProjects.size();
            }

            // 11. Contact Messages (Append or restore)
            if (payload.messages != null && !payload.messages.isEmpty()) {
                contactMessageRepository.deleteAll();
                payload.messages.forEach(m -> m.setId(null));
                contactMessageRepository.saveAll(payload.messages);
                restoredCount += payload.messages.size();
            }

            // 12. Site Stats — upsert by statDate to avoid duplicate key on unique constraint
            if (payload.siteStats != null && !payload.siteStats.isEmpty()) {
                int inserted = 0, updated = 0;
                for (SiteStat incoming : payload.siteStats) {
                    if (incoming.getStatDate() == null) continue;
                    java.util.Optional<SiteStat> existing = siteStatRepository.findByStatDate(incoming.getStatDate());
                    if (existing.isPresent()) {
                        SiteStat row = existing.get();
                        row.setPortfolioViews(incoming.getPortfolioViews() != null ? incoming.getPortfolioViews() : 0L);
                        row.setResumeDownloads(incoming.getResumeDownloads() != null ? incoming.getResumeDownloads() : 0L);
                        row.setMessagesReceived(incoming.getMessagesReceived() != null ? incoming.getMessagesReceived() : 0L);
                        row.setProjectClicks(incoming.getProjectClicks() != null ? incoming.getProjectClicks() : 0L);
                        siteStatRepository.save(row);
                        log.info("Restore: UPDATED site_stats row for date {}", incoming.getStatDate());
                        updated++;
                    } else {
                        incoming.setId(null);
                        siteStatRepository.save(incoming);
                        log.info("Restore: INSERTED site_stats row for date {}", incoming.getStatDate());
                        inserted++;
                    }
                }
                restoredCount += inserted + updated;
                log.info("Restore: site_stats — {} inserted, {} updated", inserted, updated);
            }

            dataVersionService.bump();
            log.info("Database restore completed successfully. Total entities restored: {}", restoredCount);
            return restoredCount;
        } catch (Exception e) {
            log.error("Database restore FAILED: {}", e.getMessage(), e);
            throw new RuntimeException("Restore failed: " + e.getMessage(), e);
        }
    }
}
