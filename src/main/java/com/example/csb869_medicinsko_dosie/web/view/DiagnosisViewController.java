package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisViewController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
        return "diagnoses/list";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisDTO());
        return "diagnoses/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("diagnosis") DiagnosisDTO dto, BindingResult br) {
        if (br.hasErrors()) return "diagnoses/form";
        diagnosisService.createDiagnosis(dto);
        return "redirect:/diagnoses";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("diagnosis", diagnosisService.getDiagnosisById(id));
        return "diagnoses/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("diagnosis") DiagnosisDTO dto, BindingResult br) {
        if (br.hasErrors()) return "diagnoses/form";
        diagnosisService.updateDiagnosis(id, dto);
        return "redirect:/diagnoses";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return "redirect:/diagnoses";
    }
}
