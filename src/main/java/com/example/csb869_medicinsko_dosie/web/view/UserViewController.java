package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.RegisterDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.PatientService;
import com.example.csb869_medicinsko_dosie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Secured("ROLE_ADMIN")
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "users/register";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("patients", patientService.getAllPatients());
            return "users/register";
        }
        userService.register(dto);
        return "redirect:/?registered=true";
    }
}
