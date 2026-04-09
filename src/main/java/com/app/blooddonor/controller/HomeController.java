package com.app.blooddonor.controller;

import com.app.blooddonor.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StatsService statsService;

    @GetMapping("/")
    public String home(Model model) {
        StatsService.HomeStats stats = statsService.getHomeStats();
        model.addAttribute("totalDonors",     stats.getTotalDonors());
        model.addAttribute("availableDonors", stats.getAvailableDonors());
        model.addAttribute("totalRequests",   stats.getTotalRequests());
        return "index";
    }

    // Static info page — no backend data needed
    @GetMapping("/compatibility")
    public String compatibility() {
        return "compatibility";
    }
}
