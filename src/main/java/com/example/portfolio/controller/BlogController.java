package com.example.portfolio.controller;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BlogPost;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;
    private final AboutInfoRepository aboutInfoRepository;

    public BlogController(BlogService blogService, AboutInfoRepository aboutInfoRepository) {
        this.blogService = blogService;
        this.aboutInfoRepository = aboutInfoRepository;
    }

    @GetMapping
    public String listPosts(@RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "q", required = false) String query,
                            Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<BlogPost> allPosts = blogService.getAllPublished();

        // Extract distinct tags across all published posts
        Set<String> allTags = new HashSet<>();
        for (BlogPost p : allPosts) {
            if (p.getTags() != null && !p.getTags().trim().isEmpty()) {
                for (String t : p.getTags().split(",")) {
                    allTags.add(t.trim());
                }
            }
        }

        // Apply filters
        List<BlogPost> filtered = allPosts;
        if (tag != null && !tag.trim().isEmpty()) {
            final String filterTag = tag.trim().toLowerCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(p -> p.getTags() != null && p.getTags().toLowerCase(Locale.ROOT).contains(filterTag))
                    .collect(Collectors.toList());
        }
        if (query != null && !query.trim().isEmpty()) {
            final String q = query.trim().toLowerCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase(Locale.ROOT).contains(q))
                            || (p.getSummary() != null && p.getSummary().toLowerCase(Locale.ROOT).contains(q))
                            || (p.getContent() != null && p.getContent().toLowerCase(Locale.ROOT).contains(q)))
                    .collect(Collectors.toList());
        }

        model.addAttribute("about", about);
        model.addAttribute("posts", filtered);
        model.addAttribute("allTags", allTags);
        model.addAttribute("selectedTag", tag);
        model.addAttribute("searchQuery", query);
        return "blog/list";
    }

    @GetMapping("/{slug}")
    public String viewPost(@PathVariable("slug") String slug, Model model) {
        Optional<BlogPost> postOpt = blogService.getBySlug(slug);
        if (postOpt.isEmpty()) {
            return "redirect:/blog";
        }

        BlogPost post = postOpt.get();
        blogService.incrementViewCount(post.getId());

        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<BlogPost> recentPosts = blogService.getRecentPublished(4).stream()
                .filter(p -> !p.getId().equals(post.getId()))
                .collect(Collectors.toList());

        model.addAttribute("about", about);
        model.addAttribute("post", post);
        model.addAttribute("recentPosts", recentPosts);
        return "blog/view";
    }
}
