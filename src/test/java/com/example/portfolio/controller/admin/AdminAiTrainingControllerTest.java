package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.PortfolioAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminAiTrainingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiTrainingRepository aiTrainingRepository;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private PortfolioAiService portfolioAiService;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminAiTrainingController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testIndexView() throws Exception {
        AiTraining rule = new AiTraining("What is your rate?", "$50/hr", "Hiring", "rate, price");
        when(aiTrainingRepository.findAllByOrderBySortOrderAscIdDesc()).thenReturn(List.of(rule));
        when(aboutInfoRepository.findAll()).thenReturn(List.of(new AboutInfo()));
        when(portfolioAiService.isGeminiConfigured()).thenReturn(true);
        when(portfolioAiService.resolveGeminiModel()).thenReturn("gemini-2.5-flash");

        mockMvc.perform(get("/admin/ai-training"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/ai/training"))
                .andExpect(model().attributeExists("trainings", "totalCount", "activeCount", "geminiConfigured", "geminiModel"));
    }

    @Test
    void testSaveNewRule() throws Exception {
        mockMvc.perform(post("/admin/ai-training/save")
                        .param("questionTrigger", "Do you know Docker?")
                        .param("correctAnswer", "Yes, Prince uses Docker for containerizing Spring Boot and MySQL services.")
                        .param("category", "Skills")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/ai-training"));

        verify(aiTrainingRepository).save(any(AiTraining.class));
        verify(dataVersionService).bump();
    }

    @Test
    void testToggleRule() throws Exception {
        AiTraining rule = new AiTraining("Query", "Answer", "General", "");
        rule.setId(10L);
        rule.setActive(true);
        when(aiTrainingRepository.findById(10L)).thenReturn(Optional.of(rule));

        mockMvc.perform(post("/admin/ai-training/toggle/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/ai-training"));

        verify(aiTrainingRepository).save(rule);
        verify(dataVersionService).bump();
    }

    @Test
    void testDeleteRule() throws Exception {
        when(aiTrainingRepository.existsById(5L)).thenReturn(true);

        mockMvc.perform(post("/admin/ai-training/delete/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/ai-training"));

        verify(aiTrainingRepository).deleteById(5L);
        verify(dataVersionService).bump();
    }

    @Test
    void testTestQueryEndpoint() throws Exception {
        when(portfolioAiService.answerWithDetails("What is your salary?"))
                .thenReturn(new PortfolioAiService.AiAnswerResult("Competitive market rate", "TRAINED_OVERRIDE", "Trained Knowledge Base"));

        mockMvc.perform(post("/admin/ai-training/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"What is your salary?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.reply").value("Competitive market rate"))
                .andExpect(jsonPath("$.source").value("TRAINED_OVERRIDE"));
    }
}
