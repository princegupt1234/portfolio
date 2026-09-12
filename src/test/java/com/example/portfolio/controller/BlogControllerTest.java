package com.example.portfolio.controller;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BlogPost;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class BlogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BlogService blogService;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @InjectMocks
    private BlogController blogController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(blogController).build();
    }

    @Test
    void testListPostsRendersBlogView() throws Exception {
        BlogPost post = new BlogPost();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setSlug("test-post");
        post.setTags("Java, Spring");

        when(aboutInfoRepository.findAll()).thenReturn(List.of(new AboutInfo()));
        when(blogService.getAllPublished()).thenReturn(List.of(post));

        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/list"))
                .andExpect(model().attributeExists("posts", "about", "allTags"));
    }

    @Test
    void testViewPostExistingSlug() throws Exception {
        BlogPost post = new BlogPost();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setSlug("test-post");

        when(blogService.getBySlug("test-post")).thenReturn(Optional.of(post));
        when(aboutInfoRepository.findAll()).thenReturn(List.of(new AboutInfo()));

        mockMvc.perform(get("/blog/test-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/view"))
                .andExpect(model().attribute("post", post));

        verify(blogService).incrementViewCount(1L);
    }

    @Test
    void testViewPostUnknownSlugRedirects() throws Exception {
        when(blogService.getBySlug("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/blog/unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog"));
    }
}
