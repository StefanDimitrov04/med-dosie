package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.PublicRegisterDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class PublicRegisterController {

    private final UserService userService;
    private final DoctorService doctorService;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("registerDTO", new PublicRegisterDTO());
        model.addAttribute("doctors", doctorService.getGeneralPractitioners());
        model.addAttribute("allDoctors", doctorService.getAllDoctors());
        return "register";
    }

    @PostMapping
    public String register(@Valid PublicRegisterDTO registerDTO, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("doctors", doctorService.getGeneralPractitioners());
            model.addAttribute("allDoctors", doctorService.getAllDoctors());
            return "register";
        }
        try {
            userService.registerPublic(registerDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("doctors", doctorService.getGeneralPractitioners());
            model.addAttribute("allDoctors", doctorService.getAllDoctors());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
