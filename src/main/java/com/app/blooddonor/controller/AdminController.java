package com.app.blooddonor.controller;

import com.app.blooddonor.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            model.addAttribute("totalUsers",      adminService.getTotalUsers());
            model.addAttribute("totalDonors",     adminService.getTotalDonors());
            model.addAttribute("availableDonors", adminService.getAvailableDonors());
            model.addAttribute("totalRequests",   adminService.getTotalRequests());
            model.addAttribute("pendingRequests", adminService.getPendingRequests());

            // Serialize to JSON — passed via th:utext, Thymeleaf never parses the content
            String requestsJson = objectMapper.writeValueAsString(adminService.getAllRequests());
            String donorsJson   = objectMapper.writeValueAsString(adminService.getAllDonors());

            model.addAttribute("requestsJson", requestsJson);
            model.addAttribute("donorsJson",   donorsJson);

        } catch (Exception e) {
            log.error("Admin dashboard error", e);
            model.addAttribute("requestsJson", "[]");
            model.addAttribute("donorsJson",   "[]");
        }
        return "admin/dashboard";
    }

    @PostMapping("/request/fulfill/{id}")
    public String fulfillRequest(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adminService.fulfillRequest(id);
            ra.addFlashAttribute("success", "Request marked as fulfilled.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/donor/delete/{id}")
    public String deleteDonor(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adminService.deleteDonor(id);
            ra.addFlashAttribute("success", "Donor removed successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
