package com.example.portfolio.controller;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioAiService portfolioAiService;

    @InjectMocks
    private AiChatController aiChatController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiChatController).build();
    }

    @Test
    void testChatReturnsValidResponse() throws Exception {
        when(portfolioAiService.answer(anyString())).thenReturn("Prince is an experienced Java & Spring Boot engineer.");

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"What are Prince's skills?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.reply").value("Prince is an experienced Java & Spring Boot engineer."));
    }

    @Test
    void testChatHandlesEmptyMessage() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("empty"));
    }
}
