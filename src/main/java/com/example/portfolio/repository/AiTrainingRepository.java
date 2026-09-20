package com.example.portfolio.repository;

import com.example.portfolio.entity.AiTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiTrainingRepository extends JpaRepository<AiTraining, Long> {
    List<AiTraining> findByActiveTrueOrderBySortOrderAscIdDesc();
    List<AiTraining> findAllByOrderBySortOrderAscIdDesc();
}
