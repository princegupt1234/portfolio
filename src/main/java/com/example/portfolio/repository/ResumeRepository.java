package com.example.portfolio.repository;

import com.example.portfolio.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByOrderByUploadedAtDesc();
    Optional<Resume> findByActiveTrue();
}
