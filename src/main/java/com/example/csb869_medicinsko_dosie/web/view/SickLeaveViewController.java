package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.data.repo.UserRepository;
import com.example.csb869_medicinsko_dosie.dto.SickLeaveDTO;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import com.example.csb869_medicinsko_dosie.service.SickLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveViewController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model, Authentication auth) {
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        List<SickLeaveDTO> leaves;
        if (isPatient) {
            Long patientId = userRepository.findByUsername(auth.getName())
                    .map(u -> u.getPatient() != null ? u.getPatient().getId() : null)
                    .orElse(null);
            if (patientId != null) {
                leaves = examinationService.getExaminationsByPatient(patientId).stream()
                        .flatMap(e -> sickLeaveService.getSickLeavesByExamination(e.getId()).stream())
                        .toList();
            } else {
                leaves = List.of();
            }
        } else {
            leaves = sickLeaveService.getAllSickLeaves();
        }
        model.addAttribute("sickLeaves", leaves);
        return "sick-leaves/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("sickLeave", sickLeaveService.getSickLeaveById(id));
        return "sick-leaves/detail";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("sickLeave", new SickLeaveDTO());
        model.addAttribute("examinations", examinationService.getAllExaminations());
        return "sick-leaves/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("sickLeave") SickLeaveDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("examinations", examinationService.getAllExaminations());
            return "sick-leaves/form";
        }
        sickLeaveService.createSickLeave(dto);
        return "redirect:/sick-leaves";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("sickLeave", sickLeaveService.getSickLeaveById(id));
        model.addAttribute("examinations", examinationService.getAllExaminations());
        return "sick-leaves/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("sickLeave") SickLeaveDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("examinations", examinationService.getAllExaminations());
            return "sick-leaves/form";
        }
        sickLeaveService.updateSickLeave(id, dto);
        return "redirect:/sick-leaves";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return "redirect:/sick-leaves";
    }
}
