package com.app.blooddonor.controller;

import com.app.blooddonor.dto.DonorDTO;
import com.app.blooddonor.dto.DonorMapper;
import com.app.blooddonor.dto.UserDTO;
import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.model.User;
import com.app.blooddonor.service.DonorService;
import com.app.blooddonor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/donor")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService  donorService;
    private final UserService   userService;
    private final DonorMapper   donorMapper;

    private static final List<String> BLOOD_GROUPS =
        List.of("A+","A-","B+","B-","O+","O-","AB+","AB-");

    // ── Show donor profile setup form ──────────────────────
    @GetMapping("/register")
    public String registerForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (donorService.findByUser(user).isPresent()) {
            return "redirect:/donor/dashboard";
        }
        model.addAttribute("profile",     new DonorProfile());
        model.addAttribute("bloodGroups", BLOOD_GROUPS);
        return "donor/register";
    }

    // ── Save new donor profile ─────────────────────────────
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("profile") DonorProfile profile,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("bloodGroups", BLOOD_GROUPS);
            return "donor/register";
        }
        try {
            User user = userService.findByEmail(principal.getName());
            donorService.registerProfile(user, profile);
            ra.addFlashAttribute("success", "Donor profile created successfully!");
            return "redirect:/donor/dashboard";
        } catch (Exception e) {
            model.addAttribute("error",       e.getMessage());
            model.addAttribute("bloodGroups", BLOOD_GROUPS);
            return "donor/register";
        }
    }

    // ── Donor dashboard ────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        // Convert to DTOs — never send raw entities to frontend
        UserDTO userDTO = donorMapper.toUserDTO(user);
        model.addAttribute("user", userDTO);

        Optional<DonorProfile> profileOpt = donorService.findByUser(user);
        if (profileOpt.isPresent()) {
            DonorDTO donorDTO = donorMapper.toDTO(profileOpt.get());
            model.addAttribute("profile",    donorDTO);
            model.addAttribute("hasProfile", true);
        } else {
            model.addAttribute("profile",    null);
            model.addAttribute("hasProfile", false);
        }
        return "donor/dashboard";
    }

    // ── Show edit form ─────────────────────────────────────
    @GetMapping("/edit")
    public String editForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        // Raw entity for form binding (needed to save back to DB)
        DonorProfile profile = donorService.findByUser(user)
            .orElse(new DonorProfile());
        model.addAttribute("profile",     profile);
        model.addAttribute("bloodGroups", BLOOD_GROUPS);
        return "donor/edit";
    }

    // ── Save edited profile ────────────────────────────────
    @PostMapping("/edit")
    public String edit(
            @Valid @ModelAttribute("profile") DonorProfile profile,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("bloodGroups", BLOOD_GROUPS);
            return "donor/edit";
        }
        try {
            User user = userService.findByEmail(principal.getName());
            donorService.updateProfile(user, profile);
            ra.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/donor/dashboard";
        } catch (Exception e) {
            model.addAttribute("error",       e.getMessage());
            model.addAttribute("bloodGroups", BLOOD_GROUPS);
            return "donor/edit";
        }
    }

    // ── Toggle availability ────────────────────────────────
    @PostMapping("/toggle")
    public String toggle(Principal principal, RedirectAttributes ra) {
        User user = userService.findByEmail(principal.getName());
        donorService.toggleAvailability(user);
        ra.addFlashAttribute("success", "Availability updated!");
        return "redirect:/donor/dashboard";
    }
}
