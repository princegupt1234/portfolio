package com.example.portfolio.controller;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PwaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @InjectMocks
    private PwaController pwaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pwaController).build();
    }

    @Test
    void returnsManifestWithConfiguredPwaSettings() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setPwaAppName("Prince Gupt Portfolio App");
        info.setPwaShortName("Prince App");
        info.setPwaThemeColor("#0a0f1d");
        info.setPwaBackgroundColor("#060913");

        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));

        mockMvc.perform(get("/manifest.webmanifest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Prince Gupt Portfolio App"))
                .andExpect(jsonPath("$.short_name").value("Prince App"))
                .andExpect(jsonPath("$.theme_color").value("#0a0f1d"))
                .andExpect(jsonPath("$.background_color").value("#060913"))
                .andExpect(jsonPath("$.display").value("standalone"))
                .andExpect(jsonPath("$.icons").isArray());
    }
}
