package com.app.blooddonor.controller;

import com.app.blooddonor.model.User;
import com.app.blooddonor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error  != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("msg",   "You have been logged out.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
            ra.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
