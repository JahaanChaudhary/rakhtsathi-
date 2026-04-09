package com.app.blooddonor.config;

import com.app.blooddonor.exception.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, Model model) {
        model.addAttribute("code",    "404");
        model.addAttribute("title",   "Not Found");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public String handleDuplicate(DuplicateEmailException e, Model model) {
        model.addAttribute("code",    "409");
        model.addAttribute("title",   "Already Registered");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(DonorProfileNotFoundException.class)
    public String handleDonorNotFound(DonorProfileNotFoundException e, Model model) {
        model.addAttribute("code",    "404");
        model.addAttribute("title",   "Profile Not Found");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model) {
        model.addAttribute("code",    "404");
        model.addAttribute("title",   "Page Not Found");
        model.addAttribute("message", "The page you are looking for does not exist.");
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handle403(Model model) {
        model.addAttribute("code",    "403");
        model.addAttribute("title",   "Access Denied");
        model.addAttribute("message", "You do not have permission to view this page.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception e, Model model) {
        model.addAttribute("code",    "500");
        model.addAttribute("title",   "Something Went Wrong");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        return "error";
    }
}
