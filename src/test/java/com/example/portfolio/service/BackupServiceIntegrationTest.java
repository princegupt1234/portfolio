package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.entity.ContactMessageStatus;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.Resume;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.entity.VisitorClassification;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.h2.api.Trigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BackupServiceIntegrationTest {

    @Autowired private BackupService backupService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private AboutInfoRepository aboutInfoRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ExperienceRepository experienceRepository;
    @Autowired private EducationEntryRepository educationEntryRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private ServiceItemRepository serviceItemRepository;
    @Autowired private TestimonialRepository testimonialRepository;
    @Autowired private BuildingProjectRepository buildingProjectRepository;
    @Autowired private LearningProjectRepository learningProjectRepository;
    @Autowired private ContactMessageRepository contactMessageRepository;
    @Autowired private SiteStatRepository siteStatRepository;
    @Autowired private AiTrainingRepository aiTrainingRepository;
    @Autowired private ResumeRepository resumeRepository;
    @Autowired private VisitorClassificationRepository visitorClassificationRepository;

    @TempDir
    Path uploadRoot;

    private Path resumeAsset;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(backupService, "uploadDir", uploadRoot.toString());
        clearApplicationRecords();
        seedAllCategories();
        resumeAsset = uploadRoot.resolve("resume/prince.pdf");
        Files.createDirectories(resumeAsset.getParent());
        Files.writeString(resumeAsset, "test resume bytes", StandardCharsets.UTF_8);
    }

    @Test
    void completeBackupRestoresEverySupportedCategoryIdsSettingsAndUploadedFiles() throws Exception {
        long adminCountBefore = adminRepository.count();
        byte[] backup = backupService.exportBackupBytes();
        JsonNode json = objectMapper.readTree(backup);

        assertEquals("2.1", json.get("schemaVersion").asText());
        assertTrue(json.has("aboutInfos"));
        assertTrue(json.has("aiTrainingRules"));
        assertTrue(json.has("resumes"));
        assertTrue(json.has("files"));
        assertTrue(json.has("visitorVisits"));
        assertFalse(new String(backup, StandardCharsets.UTF_8).contains("test-gemini-secret"));
        assertFalse(new String(backup, StandardCharsets.UTF_8).contains("test-webhook-secret"));
        assertEquals(1, json.get("recordCounts").get("aboutInfos").asInt());
        assertEquals(1, json.get("recordCounts").get("aiTrainingRules").asInt());
        assertEquals(1, json.get("recordCounts").get("resumes").asInt());
        assertEquals(1, json.get("recordCounts").get("files").asInt());
        assertEquals(1, json.get("recordCounts").get("visitorVisits").asInt());
        Map<String, Long> sourceCounts = backupService.getEntityCounts();
        Iterator<String> sourceCategoryNames = json.get("recordCounts").fieldNames();
        while (sourceCategoryNames.hasNext()) {
            String category = sourceCategoryNames.next();
            assertEquals(json.get("recordCounts").get(category).asLong(), sourceCounts.get(category));
        }

        Long projectId = json.get("projects").get(0).get("id").asLong();
        Long trainingId = json.get("aiTrainingRules").get(0).get("id").asLong();
        Long resumeId = json.get("resumes").get(0).get("id").asLong();
        clearApplicationRecords();

        BackupService.RestoreResult result =
                backupService.restoreBackup(new ByteArrayInputStream(backup));

        assertEquals(1, projectRepository.count());
        assertEquals(projectId, projectRepository.findAll().get(0).getId());
        assertFalse(projectRepository.findAll().get(0).getVisible());
        assertEquals(1, aiTrainingRepository.count());
        assertEquals(trainingId, aiTrainingRepository.findAll().get(0).getId());
        assertFalse(aiTrainingRepository.findAll().get(0).getActive());
        assertEquals(1, resumeRepository.count());
        assertEquals(resumeId, resumeRepository.findAll().get(0).getId());
        assertFalse(resumeRepository.findAll().get(0).getActive());
        assertEquals("प्रिंस ✨", aboutInfoRepository.findAll().get(0).getFullName());
        assertEquals(1, siteStatRepository.count());
        assertEquals(adminCountBefore, adminRepository.count());
        assertEquals("test resume bytes", Files.readString(resumeAsset, StandardCharsets.UTF_8));
        assertEquals(16, result.restoredCounts().size());
        assertEquals(1, visitorClassificationRepository.count());
        assertEquals("00112233445566778899aabbccddeeff", contactMessageRepository.findAll().get(0).getReferenceId());
        assertEquals(ContactMessageStatus.ARCHIVED, contactMessageRepository.findAll().get(0).getStatus());
        assertTrue(result.skippedCategories().containsKey("admins"));

        Map<String, Long> restoredCounts = backupService.getEntityCounts();
        Iterator<String> categoryNames = json.get("recordCounts").fieldNames();
        while (categoryNames.hasNext()) {
            String category = categoryNames.next();
            assertEquals(json.get("recordCounts").get(category).asLong(), restoredCounts.get(category));
        }
    }

    @Test
    void currentVersionEmptyCategoryIsAuthoritativeAndClearsExistingRecords() throws Exception {
        ObjectNode backup = (ObjectNode) objectMapper.readTree(backupService.exportBackupBytes());
        backup.putArray("skills");
        ((ObjectNode) backup.get("recordCounts")).put("skills", 0);

        backupService.restoreBackup(new ByteArrayInputStream(objectMapper.writeValueAsBytes(backup)));

        assertEquals(0L, skillRepository.count());
        assertEquals(1L, projectRepository.count());
    }

    @Test
    void restorePreservesConflictingUploadsAndUpdatesRestoredReferences() throws Exception {
        byte[] backup = backupService.exportBackupBytes();
        Files.writeString(resumeAsset, "current production-side file", StandardCharsets.UTF_8);

        backupService.restoreBackup(new ByteArrayInputStream(backup));

        Resume restoredResume = resumeRepository.findAll().get(0);
        assertTrue(restoredResume.getFileUrl().startsWith("/uploads/restored/"));
        Path restoredAsset = uploadRoot.resolve(restoredResume.getFileUrl().substring("/uploads/".length()));
        assertEquals("test resume bytes", Files.readString(restoredAsset, StandardCharsets.UTF_8));
        assertEquals("current production-side file", Files.readString(resumeAsset, StandardCharsets.UTF_8));
    }

    @Test
    void legacyBackupRestoresExportedCategoriesAndPreservesCategoriesItNeverContained() throws Exception {
        byte[] fullBackup = backupService.exportBackupBytes();
        ObjectNode legacy = (ObjectNode) objectMapper.readTree(fullBackup);
        legacy.put("schemaVersion", "1.0");
        legacy.set("aboutInfo", legacy.get("aboutInfos").get(0));
        legacy.remove("aboutInfos");
        legacy.remove("aiTrainingRules");
        legacy.remove("resumes");
        legacy.remove("files");
        legacy.remove("visitorVisits");
        legacy.remove("excludedCategories");
        ObjectNode oldCounts = (ObjectNode) legacy.get("recordCounts");
        oldCounts.remove("aboutInfos");
        oldCounts.remove("siteStats");
        oldCounts.remove("aiTrainingRules");
        oldCounts.remove("resumes");
        oldCounts.remove("files");
        oldCounts.remove("visitorVisits");
        byte[] legacyBackup = objectMapper.writeValueAsBytes(legacy);

        Long originalTrainingId = aiTrainingRepository.findAll().get(0).getId();
        Long originalResumeId = resumeRepository.findAll().get(0).getId();
        BackupService.RestoreResult result =
                backupService.restoreBackup(new ByteArrayInputStream(legacyBackup));

        assertEquals(originalTrainingId, aiTrainingRepository.findAll().get(0).getId());
        assertEquals(originalResumeId, resumeRepository.findAll().get(0).getId());
        assertTrue(result.skippedCategories().containsKey("aiTrainingRules"));
        assertTrue(result.skippedCategories().containsKey("resumes"));
        assertTrue(result.skippedCategories().containsKey("files"));
        assertTrue(result.skippedCategories().containsKey("visitorVisits"));
        assertEquals(1, projectRepository.count());
    }

    @Test
    void rejectsInvalidVersionWithoutChangingDataAndRollsBackDatabaseAndFilesOnRestoreFailure() throws Exception {
        long projectsBefore = projectRepository.count();
        ObjectNode unsupported = (ObjectNode) objectMapper.readTree(backupService.exportBackupBytes());
        unsupported.put("schemaVersion", "99.0");
        assertThrows(IllegalStateException.class, () ->
                backupService.restoreBackup(new ByteArrayInputStream(objectMapper.writeValueAsBytes(unsupported))));
        assertThrows(IllegalStateException.class, () ->
                backupService.restoreBackup(new ByteArrayInputStream("{invalid".getBytes(StandardCharsets.UTF_8))));
        assertEquals(projectsBefore, projectRepository.count());

        ObjectNode failingBackup = (ObjectNode) objectMapper.readTree(backupService.exportBackupBytes());
        ArrayNode files = (ArrayNode) failingBackup.get("files");
        ObjectNode newFile = objectMapper.createObjectNode();
        newFile.put("path", "rollback/created.txt");
        newFile.put("contentBase64", Base64.getEncoder().encodeToString("temporary".getBytes(StandardCharsets.UTF_8)));
        files.add(newFile);
        ((ObjectNode) failingBackup.get("recordCounts")).put("files", files.size());

        jdbcTemplate.execute("CREATE TRIGGER backup_restore_test_failure BEFORE INSERT ON projects FOR EACH ROW CALL '"
                + FailProjectInsertTrigger.class.getName() + "'");
        try {
            assertThrows(IllegalStateException.class, () ->
                    backupService.restoreBackup(new ByteArrayInputStream(objectMapper.writeValueAsBytes(failingBackup))));
        } finally {
            jdbcTemplate.execute("DROP TRIGGER backup_restore_test_failure");
        }

        assertEquals(projectsBefore, projectRepository.count());
        assertFalse(Files.exists(uploadRoot.resolve("rollback/created.txt")));
        assertEquals("test resume bytes", Files.readString(resumeAsset, StandardCharsets.UTF_8));
    }

    private void clearApplicationRecords() {
        aiTrainingRepository.deleteAllInBatch();
        resumeRepository.deleteAllInBatch();
        siteStatRepository.deleteAllInBatch();
        visitorClassificationRepository.deleteAllInBatch();
        contactMessageRepository.deleteAllInBatch();
        learningProjectRepository.deleteAllInBatch();
        buildingProjectRepository.deleteAllInBatch();
        testimonialRepository.deleteAllInBatch();
        serviceItemRepository.deleteAllInBatch();
        certificateRepository.deleteAllInBatch();
        educationEntryRepository.deleteAllInBatch();
        experienceRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        skillRepository.deleteAllInBatch();
        aboutInfoRepository.deleteAllInBatch();
    }

    private void seedAllCategories() {
        AboutInfo about = new AboutInfo();
        about.setFullName("प्रिंस ✨");
        about.setBio("Restore fixture");
        about.setGeminiApiKey("test-gemini-secret");
        about.setWhatsappNotificationWebhookUrl("test-webhook-secret");
        aboutInfoRepository.save(about);

        Skill skill = new Skill();
        skill.setName("Java");
        skill.setCategory("Backend");
        skill.setProficiency(90);
        skill.setVisible(false);
        skillRepository.save(skill);

        Project project = new Project();
        project.setTitle("Backup fixture");
        project.setVisible(false);
        project.setImageUrl("/uploads/resume/prince.pdf");
        projectRepository.save(project);

        Experience experience = new Experience();
        experience.setCompany("Fixture Company");
        experience.setRole("Engineer");
        experienceRepository.save(experience);

        EducationEntry education = new EducationEntry();
        education.setDegree("B.Tech");
        education.setInstitution("Fixture University");
        education.setDuration("2020-2024");
        educationEntryRepository.save(education);

        Certificate certificate = new Certificate();
        certificate.setTitle("Fixture Certificate");
        certificateRepository.save(certificate);

        ServiceItem service = new ServiceItem();
        service.setTitle("Fixture Service");
        serviceItemRepository.save(service);

        Testimonial testimonial = new Testimonial();
        testimonial.setName("Fixture Client");
        testimonial.setRating(5);
        testimonialRepository.save(testimonial);

        BuildingProject buildingProject = new BuildingProject();
        buildingProject.setTitle("Fixture Build");
        buildingProjectRepository.save(buildingProject);

        LearningProject learningProject = new LearningProject();
        learningProject.setTitle("Fixture Learning");
        learningProjectRepository.save(learningProject);

        ContactMessage message = new ContactMessage();
        message.setName("Fixture Sender");
        message.setEmail("fixture@example.com");
        message.setMessage("Backup verification");
        message.setReferenceId("00112233445566778899aabbccddeeff");
        message.setStatus(ContactMessageStatus.ARCHIVED);
        contactMessageRepository.save(message);

        SiteStat stat = new SiteStat();
        stat.setStatDate(LocalDate.of(2026, 10, 1));
        stat.setPortfolioViews(17L);
        siteStatRepository.save(stat);

        VisitorClassificationRecord visitor = new VisitorClassificationRecord();
        visitor.setVisitDate(LocalDate.of(2026, 10, 1));
        visitor.setVisitorKeyHash("a".repeat(64));
        visitor.setClassification(VisitorClassification.BOT);
        visitorClassificationRepository.save(visitor);

        AiTraining training = new AiTraining("Correction trigger", "Correction answer", "Correction", "trigger");
        training.setActive(false);
        aiTrainingRepository.save(training);

        Resume resume = new Resume();
        resume.setFileUrl("/uploads/resume/prince.pdf");
        resume.setVersionLabel("fixture");
        resume.setActive(false);
        resumeRepository.save(resume);
    }

    public static class FailProjectInsertTrigger implements Trigger {
        @Override
        public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
            throw new SQLException("simulated backup restore failure");
        }
    }
}
