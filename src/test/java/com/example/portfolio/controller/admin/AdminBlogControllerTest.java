package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.BlogPost;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminBlogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BlogService blogService;

    @InjectMocks
    private AdminBlogController adminBlogController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminBlogController).build();
    }

    @Test
    void testListReturnsAdminListView() throws Exception {
        when(blogService.getAllForAdmin()).thenReturn(List.of(new BlogPost()));

        mockMvc.perform(get("/admin/blogs"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/blogs/list"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void testNewFormReturnsFormView() throws Exception {
        mockMvc.perform(get("/admin/blogs/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/blogs/form"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void testSavePostRedirectsWithSuccess() throws Exception {
        BlogPost post = new BlogPost();
        post.setTitle("Sample Post");
        when(blogService.save(any(BlogPost.class))).thenReturn(post);

        mockMvc.perform(post("/admin/blogs/save")
                        .param("title", "Sample Post")
                        .param("content", "Sample content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/blogs"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testDeletePost() throws Exception {
        BlogPost post = new BlogPost();
        post.setId(10L);
        post.setTitle("To Delete");

        when(blogService.getById(10L)).thenReturn(Optional.of(post));

        mockMvc.perform(post("/admin/blogs/10/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/blogs"));

        verify(blogService).delete(10L);
    }
}
