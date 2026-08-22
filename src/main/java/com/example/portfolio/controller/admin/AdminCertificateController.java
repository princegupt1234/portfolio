package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Certificate;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/certificates")
public class AdminCertificateController {

    private final CertificateRepository certificateRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    public AdminCertificateController(CertificateRepository certificateRepository, FileStorageService fileStorageService, DataVersionService dataVersionService) {
        this.certificateRepository = certificateRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("certificates", certificateRepository.findAllByOrderBySortOrderAsc());
        return "admin/certificates/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("certificate", new Certificate());
        return "admin/certificates/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("certificate", certificateRepository.findById(id).orElseThrow());
        return "admin/certificates/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Certificate certificate,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            certificate.setImageUrl(fileStorageService.store(imageFile, "certificates"));
        }
        if (pdfFile != null && !pdfFile.isEmpty()) {
            certificate.setPdfUrl(fileStorageService.store(pdfFile, "certificates"));
        }
        if (certificate.getId() != null) {
            Certificate existing = certificateRepository.findById(certificate.getId()).orElse(null);
            if (existing != null) {
                if (certificate.getImageUrl() == null) certificate.setImageUrl(existing.getImageUrl());
                if (certificate.getPdfUrl() == null) certificate.setPdfUrl(existing.getPdfUrl());
            }
        }
        certificateRepository.save(certificate);
        dataVersionService.bump();
        return "redirect:/admin/certificates";
    }

    @PostMapping("/{id}/toggle-visible")
    public String toggleVisible(@PathVariable("id") Long id) {
        certificateRepository.findById(id).ifPresent(c -> {
            c.setVisible(!Boolean.TRUE.equals(c.getVisible()));
            certificateRepository.save(c);
        });
        dataVersionService.bump();
        return "redirect:/admin/certificates";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        certificateRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/certificates";
    }
}
