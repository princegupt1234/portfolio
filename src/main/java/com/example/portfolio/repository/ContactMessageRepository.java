package com.example.portfolio.repository;

import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.entity.ContactMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findAllByOrderByCreatedAtDesc();
    long countByIsReadFalse();
    boolean existsByReferenceId(String referenceId);
    List<ContactMessage> findAllByStatusOrderByCreatedAtDesc(ContactMessageStatus status);

    List<ContactMessage> findAllByStatusOrStatusIsNullOrderByCreatedAtDesc(
            ContactMessageStatus status);
}
