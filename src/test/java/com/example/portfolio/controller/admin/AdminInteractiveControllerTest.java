package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminInteractiveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminInteractiveController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersInteractiveEditPage() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setAiChatWelcomeMessage("Hi there!");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));

        mockMvc.perform(get("/admin/interactive"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/interactive/edit"))
                .andExpect(model().attributeExists("about"))
                .andExpect(model().attribute("activeSub", "interactive"));
    }

    @Test
    void savesInteractiveFieldsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/interactive/save")
                        .param("aiChatEnabled", "true")
                        .param("aiChatWelcomeMessage", "Welcome to Prince Portfolio AI!")
                        .param("aiChatPromptChips", "Availability :: Immediate?")
                        .param("terminalEnabled", "true")
                        .param("customCliCommands", "[{\"cmd\":\"blog\",\"desc\":\"Articles\",\"output\":\"Link\"}]")
                        .param("ogTagsEnabled", "true")
                        .param("ogTitle", "Prince Gupt | Java Engineer")
                        .param("ogDescription", "Backend Developer portfolio")
                        .param("ogImageUrl", "/images/custom-og.png")
                        .param("contactSpamProtectionEnabled", "true")
                        .param("contactHoneypotEnabled", "true")
                        .param("contactRateLimitSeconds", "45")
                        .param("whatsappNotificationEnabled", "true")
                        .param("whatsappNotificationPhone", "919876543210")
                        .param("whatsappNotificationApiKey", "7654321")
                        .param("mailNotificationEmail", "admin@example.com")
                        .param("resendApiKey", "re_123456789")
                        .param("resendFrom", "Portfolio <onboarding@resend.dev>")
                        .param("brevoApiKey", "xkeysib-987654321")
                        .param("brevoSenderEmail", "sender@example.com")
                        .param("brevoSenderName", "Prince Portfolio")
                        .param("mailSendingMethod", "RESEND"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/interactive"));

        ArgumentCaptor<AboutInfo> captor = ArgumentCaptor.forClass(AboutInfo.class);
        verify(aboutInfoRepository).save(captor.capture());
        AboutInfo saved = captor.getValue();

        assertThat(saved.getAiChatEnabled()).isTrue();
        assertThat(saved.getAiChatWelcomeMessage()).isEqualTo("Welcome to Prince Portfolio AI!");
        assertThat(saved.getTerminalEnabled()).isTrue();
        assertThat(saved.getCustomCliCommands()).contains("blog");
        assertThat(saved.getOgTagsEnabled()).isTrue();
        assertThat(saved.getOgTitle()).isEqualTo("Prince Gupt | Java Engineer");
        assertThat(saved.getOgImageUrl()).isEqualTo("/images/custom-og.png");
        assertThat(saved.getContactSpamProtectionEnabled()).isTrue();
        assertThat(saved.getContactHoneypotEnabled()).isTrue();
        assertThat(saved.getContactRateLimitSeconds()).isEqualTo(45);
        assertThat(saved.getWhatsappNotificationEnabled()).isTrue();
        assertThat(saved.getWhatsappNotificationPhone()).isEqualTo("919876543210");
        assertThat(saved.getMailNotificationEmail()).isEqualTo("admin@example.com");
        assertThat(saved.getResendApiKey()).isEqualTo("re_123456789");
        assertThat(saved.getMailSendingMethod()).isEqualTo("RESEND");

        verify(dataVersionService).bump();
    }
}
