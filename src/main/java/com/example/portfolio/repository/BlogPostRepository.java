package com.example.portfolio.repository;

import com.example.portfolio.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Optional<BlogPost> findBySlug(String slug);

    Optional<BlogPost> findBySlugAndPublishedTrue(String slug);

    List<BlogPost> findByPublishedTrueOrderByCreatedAtDesc();

    List<BlogPost> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<BlogPost> findTop3ByPublishedTrueOrderByCreatedAtDesc();

    List<BlogPost> findAllByOrderByCreatedAtDesc();

    long countByPublishedTrue();

    boolean existsBySlug(String slug);
}
