package com.example.portfolio.controller;

import com.example.portfolio.entity.Skill;
import com.example.portfolio.repository.SkillRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SkillRepository skillRepository;

    public ApiController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping("/skills")
    public List<Skill> getVisibleSkills() {
        return skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
    }
}
