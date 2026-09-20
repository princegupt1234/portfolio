package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.PortfolioAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/ai-training")
public class AdminAiTrainingController {

    private final AiTrainingRepository aiTrainingRepository;
    private final AboutInfoRepository aboutInfoRepository;
    private final PortfolioAiService portfolioAiService;
    private final DataVersionService dataVersionService;

    public AdminAiTrainingController(AiTrainingRepository aiTrainingRepository,
                                     AboutInfoRepository aboutInfoRepository,
                                     PortfolioAiService portfolioAiService,
                                     DataVersionService dataVersionService) {
        this.aiTrainingRepository = aiTrainingRepository;
        this.aboutInfoRepository = aboutInfoRepository;
        this.portfolioAiService = portfolioAiService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String index(Model model) {
        List<AiTraining> trainings = aiTrainingRepository.findAllByOrderBySortOrderAscIdDesc();
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        long activeCount = trainings.stream().filter(t -> Boolean.TRUE.equals(t.getActive())).count();
        boolean geminiConfigured = portfolioAiService.isGeminiConfigured();
        String activeModel = portfolioAiService.resolveGeminiModel();

        model.addAttribute("trainings", trainings);
        model.addAttribute("about", about);
        model.addAttribute("totalCount", trainings.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("geminiConfigured", geminiConfigured);
        model.addAttribute("geminiModel", activeModel);
        model.addAttribute("activeSub", "ai-training");
        return "admin/ai/training";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AiTraining form,
                       @RequestParam(value = "active", required = false) String activeParam,
                       RedirectAttributes redirectAttributes) {
        if (form.getQuestionTrigger() == null || form.getQuestionTrigger().isBlank()
                || form.getCorrectAnswer() == null || form.getCorrectAnswer().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Both Question Trigger and Correct Answer are required.");
            return "redirect:/admin/ai-training";
        }

        boolean isActive = "true".equalsIgnoreCase(activeParam) || "on".equalsIgnoreCase(activeParam);

        if (form.getId() != null) {
            AiTraining existing = aiTrainingRepository.findById(form.getId()).orElse(null);
            if (existing != null) {
                existing.setQuestionTrigger(form.getQuestionTrigger().trim());
                existing.setCorrectAnswer(form.getCorrectAnswer().trim());
                existing.setCategory(form.getCategory() != null && !form.getCategory().isBlank() ? form.getCategory().trim() : "General");
                existing.setMatchKeywords(form.getMatchKeywords() != null ? form.getMatchKeywords().trim() : "");
                existing.setActive(isActive);
                if (form.getSortOrder() != null) existing.setSortOrder(form.getSortOrder());
                aiTrainingRepository.save(existing);
                redirectAttributes.addFlashAttribute("successMessage", "AI Training rule updated successfully.");
            }
        } else {
            form.setQuestionTrigger(form.getQuestionTrigger().trim());
            form.setCorrectAnswer(form.getCorrectAnswer().trim());
            if (form.getCategory() == null || form.getCategory().isBlank()) form.setCategory("General");
            form.setActive(isActive);
            if (form.getSortOrder() == null) form.setSortOrder(0);
            aiTrainingRepository.save(form);
            redirectAttributes.addFlashAttribute("successMessage", "New AI Training rule created successfully.");
        }

        dataVersionService.bump();
        return "redirect:/admin/ai-training";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AiTraining existing = aiTrainingRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.getActive()));
            aiTrainingRepository.save(existing);
            dataVersionService.bump();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Training rule " + (existing.getActive() ? "enabled" : "disabled") + " successfully.");
        }
        return "redirect:/admin/ai-training";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (aiTrainingRepository.existsById(id)) {
            aiTrainingRepository.deleteById(id);
            dataVersionService.bump();
            redirectAttributes.addFlashAttribute("successMessage", "Training rule deleted successfully.");
        }
        return "redirect:/admin/ai-training";
    }

    @PostMapping("/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testAnswer(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("query", "");
        if (query.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "status", "empty",
                    "reply", "Please type a question to test how the AI will respond."
            ));
        }

        PortfolioAiService.AiAnswerResult res = portfolioAiService.answerWithDetails(query);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "query", query,
                "reply", res.reply(),
                "source", res.source(),
                "model", res.model()
        ));
    }

    @PostMapping("/settings")
    public String updateSettings(@RequestParam(value = "geminiApiKey", required = false) String geminiApiKey,
                                 @RequestParam(value = "geminiModel", required = false) String geminiModel,
                                 @RequestParam(value = "aiCustomInstructions", required = false) String aiCustomInstructions,
                                 RedirectAttributes redirectAttributes) {
        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        if (geminiApiKey != null) {
            existing.setGeminiApiKey(geminiApiKey.trim());
        }
        if (geminiModel != null && !geminiModel.isBlank()) {
            existing.setGeminiModel(geminiModel.trim());
        }
        if (aiCustomInstructions != null) {
            existing.setAiCustomInstructions(aiCustomInstructions.trim());
        }
        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "AI & Gemini configuration saved successfully.");
        return "redirect:/admin/ai-training";
    }
}
