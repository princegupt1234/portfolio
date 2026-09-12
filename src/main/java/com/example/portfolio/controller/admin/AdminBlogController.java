package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.BlogPost;
import com.example.portfolio.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/blogs")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", blogService.getAllForAdmin());
        return "admin/blogs/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        BlogPost post = new BlogPost();
        post.setPublished(true);
        post.setFeatured(false);
        post.setReadTimeMinutes(5);
        model.addAttribute("post", post);
        return "admin/blogs/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        BlogPost post = blogService.getById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        return "admin/blogs/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BlogPost post, RedirectAttributes redirectAttributes) {
        if (post.getPublished() == null) {
            post.setPublished(false);
        }
        if (post.getFeatured() == null) {
            post.setFeatured(false);
        }
        BlogPost saved = blogService.save(post);
        redirectAttributes.addFlashAttribute("successMessage", "Article \"" + saved.getTitle() + "\" saved successfully.");
        return "redirect:/admin/blogs";
    }

    @PostMapping("/{id}/toggle-published")
    public String togglePublished(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        blogService.getById(id).ifPresent(post -> {
            post.setPublished(!Boolean.TRUE.equals(post.getPublished()));
            blogService.save(post);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Article \"" + post.getTitle() + "\" publication status updated.");
        });
        return "redirect:/admin/blogs";
    }

    @PostMapping("/{id}/toggle-featured")
    public String toggleFeatured(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        blogService.getById(id).ifPresent(post -> {
            post.setFeatured(!Boolean.TRUE.equals(post.getFeatured()));
            blogService.save(post);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Article \"" + post.getTitle() + "\" featured status updated.");
        });
        return "redirect:/admin/blogs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        blogService.getById(id).ifPresent(post -> {
            blogService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article \"" + post.getTitle() + "\" deleted.");
        });
        return "redirect:/admin/blogs";
    }
}
