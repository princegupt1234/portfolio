package com.example.portfolio.service;

import com.example.portfolio.entity.BlogPost;
import com.example.portfolio.repository.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final DataVersionService dataVersionService;

    public BlogService(BlogPostRepository blogPostRepository, DataVersionService dataVersionService) {
        this.blogPostRepository = blogPostRepository;
        this.dataVersionService = dataVersionService;
    }

    public List<BlogPost> getAllPublished() {
        return blogPostRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<BlogPost> getRecentPublished(int limit) {
        List<BlogPost> all = blogPostRepository.findTop3ByPublishedTrueOrderByCreatedAtDesc();
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }

    public List<BlogPost> getAllForAdmin() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<BlogPost> getBySlug(String slug) {
        return blogPostRepository.findBySlugAndPublishedTrue(slug);
    }

    public Optional<BlogPost> getBySlugAdmin(String slug) {
        return blogPostRepository.findBySlug(slug);
    }

    public Optional<BlogPost> getById(Long id) {
        return blogPostRepository.findById(id);
    }

    @Transactional
    public BlogPost save(BlogPost post) {
        if (post.getSlug() == null || post.getSlug().trim().isEmpty()) {
            post.setSlug(generateSlug(post.getTitle()));
        } else {
            post.setSlug(normalizeSlug(post.getSlug()));
        }

        // Ensure slug uniqueness
        post.setSlug(ensureUniqueSlug(post.getSlug(), post.getId()));

        // Calculate read time if not explicitly provided
        if (post.getReadTimeMinutes() == null || post.getReadTimeMinutes() <= 0) {
            post.setReadTimeMinutes(calculateReadTime(post.getContent()));
        }

        BlogPost saved = blogPostRepository.save(post);
        dataVersionService.bump();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        blogPostRepository.deleteById(id);
        dataVersionService.bump();
    }

    @Transactional
    public void incrementViewCount(Long id) {
        blogPostRepository.findById(id).ifPresent(post -> {
            post.setViewCount((post.getViewCount() == null ? 0L : post.getViewCount()) + 1);
            blogPostRepository.save(post);
        });
    }

    public String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "post-" + System.currentTimeMillis();
        }
        String slug = title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        return slug.isEmpty() ? "post-" + System.currentTimeMillis() : slug;
    }

    public String normalizeSlug(String slug) {
        return slug.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private String ensureUniqueSlug(String baseSlug, Long currentId) {
        String slug = baseSlug;
        int count = 1;
        while (true) {
            Optional<BlogPost> existing = blogPostRepository.findBySlug(slug);
            if (existing.isEmpty() || (currentId != null && existing.get().getId().equals(currentId))) {
                return slug;
            }
            count++;
            slug = baseSlug + "-" + count;
        }
    }

    private int calculateReadTime(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 3;
        }
        int wordCount = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }
}
