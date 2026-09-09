package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/about")
public class AdminAboutController {

    private final AboutInfoRepository aboutInfoRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    @PersistenceContext
    private EntityManager em;

    public AdminAboutController(AboutInfoRepository aboutInfoRepository,
                                 EducationEntryRepository educationEntryRepository,
                                 BuildingProjectRepository buildingProjectRepository,
                                 LearningProjectRepository learningProjectRepository,
                                 FileStorageService fileStorageService,
                                 DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping("/migrate")
    @ResponseBody
    @Transactional
    public String migrate() {
        try {
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS availability_text VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS availability_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS footer_tagline VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS footer_sub TEXT NULL").executeUpdate();
            em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS building_project (" +
                "  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  title VARCHAR(255)," +
                "  summary TEXT," +
                "  description TEXT," +
                "  tech_stack TEXT," +
                "  project_url VARCHAR(255)," +
                "  status VARCHAR(255)," +
                "  progress INT," +
                "  sort_order INT" +
                ")"
            ).executeUpdate();
            return "Migration OK — all columns and tables created. You can now remove this endpoint.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("education", educationEntryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("buildingProjects", buildingProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("editingBuildingProject", new BuildingProject());
        model.addAttribute("learningProjects", learningProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newLearningProject", new LearningProject());
        return "admin/about/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo about,
                        @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {
        if (photoFile != null && !photoFile.isEmpty()) {
            about.setProfileImage(fileStorageService.store(photoFile, "profile"));
        } else if (about.getId() != null) {
            aboutInfoRepository.findById(about.getId())
                    .ifPresent(existing -> about.setProfileImage(existing.getProfileImage()));
        }
        aboutInfoRepository.save(about);
        dataVersionService.bump();
        return "redirect:/admin/about";
    }

    @PostMapping("/education/save")
    public String saveEducation(@ModelAttribute EducationEntry educationEntry) {
        educationEntryRepository.save(educationEntry);
        dataVersionService.bump();
        return "redirect:/admin/about";
    }

    @PostMapping("/education/{id}/delete")
    public String deleteEducation(@PathVariable("id") Long id) {
        educationEntryRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about";
    }

    @GetMapping("/building/{id}/edit")
    public String editBuildingProjectForm(@PathVariable("id") Long id, Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("education", educationEntryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("buildingProjects", buildingProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("editingBuildingProject", buildingProjectRepository.findById(id).orElseThrow());
        model.addAttribute("learningProjects", learningProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newLearningProject", new LearningProject());
        return "admin/about/edit";
    }

    @PostMapping("/building/save")
    public String saveBuildingProject(@ModelAttribute BuildingProject project) {
        buildingProjectRepository.save(project);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-building";
    }

    @PostMapping("/building/{id}/delete")
    public String deleteBuildingProject(@PathVariable("id") Long id) {
        buildingProjectRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-building";
    }

    @PostMapping("/learning/save")
    public String saveLearningProject(@ModelAttribute LearningProject project) {
        learningProjectRepository.save(project);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-learning";
    }

    @PostMapping("/learning/{id}/delete")
    public String deleteLearningProject(@PathVariable("id") Long id) {
        learningProjectRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-learning";
    }

    @PostMapping("/learning/{id}/move")
    public String moveLearningProject(@PathVariable("id") Long id, @RequestParam("dir") int dir) {
        var all = learningProjectRepository.findAllByOrderBySortOrderAsc();
        int idx = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) { idx = i; break; }
        }
        int target = idx + dir;
        if (idx >= 0 && target >= 0 && target < all.size()) {
            int tmp = all.get(idx).getSortOrder() != null ? all.get(idx).getSortOrder() : idx;
            int tgt = all.get(target).getSortOrder() != null ? all.get(target).getSortOrder() : target;
            all.get(idx).setSortOrder(tgt);
            all.get(target).setSortOrder(tmp);
            learningProjectRepository.saveAll(all);
            dataVersionService.bump();
        }
        return "redirect:/admin/about#sec-learning";
    }
}
