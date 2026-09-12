package com.example.portfolio.controller;

import com.example.portfolio.service.PortfolioAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final PortfolioAiService portfolioAiService;

    public AiChatController(PortfolioAiService portfolioAiService) {
        this.portfolioAiService = portfolioAiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        if (message.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "status", "empty",
                    "reply", "Please enter a question so I can assist you!"
            ));
        }

        String reply = portfolioAiService.answer(message);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "reply", reply
        ));
    }
}
