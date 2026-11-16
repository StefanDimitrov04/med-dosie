package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.PublicRegisterDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("allDoctors", doctorService.getAllDoctors());
        return "register";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("registerDTO") PublicRegisterDTO registerDTO,
                           BindingResult br, Model model, HttpServletRequest request,
                           jakarta.servlet.http.HttpServletResponse response) {
        // EGN is required only for PATIENT
        if ("PATIENT".equalsIgnoreCase(registerDTO.getRole())
                && (registerDTO.getEgn() == null || registerDTO.getEgn().isBlank())) {
            br.rejectValue("egn", "egn.required", "Personal ID is required for patients");
        }

        if (br.hasErrors()) {
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("allDoctors", doctorService.getAllDoctors());
            return "register";
        }
        try {
            userService.registerPublic(registerDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("allDoctors", doctorService.getAllDoctors());
            return "register";
        }

        // Auto-login: set auth and persist to HTTP session so it survives the redirect
        UserDetails userDetails = userService.loadUserByUsername(registerDTO.getUsername());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return "redirect:/";
    }
}
