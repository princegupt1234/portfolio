package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminMessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AdminMessageController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void toggleReadFromUnreadToRead() throws Exception {
        ContactMessage message = new ContactMessage();
        message.setId(10L);
        message.setName("Alice");
        message.setIsRead(false);

        when(contactMessageRepository.findById(10L)).thenReturn(Optional.of(message));

        mockMvc.perform(post("/admin/messages/10/toggle-read"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("successMessage"));

        assertTrue(message.getIsRead());
        verify(contactMessageRepository).save(message);
    }

    @Test
    void toggleReadFromReadToUnread() throws Exception {
        ContactMessage message = new ContactMessage();
        message.setId(11L);
        message.setName("Bob");
        message.setIsRead(true);

        when(contactMessageRepository.findById(11L)).thenReturn(Optional.of(message));

        mockMvc.perform(post("/admin/messages/11/toggle-read"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("successMessage"));

        assertFalse(message.getIsRead());
        verify(contactMessageRepository).save(message);
    }
}
