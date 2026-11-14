package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.PatientDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientViewController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "patients/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("patient", new PatientDTO());
        model.addAttribute("doctors", doctorService.getGeneralPractitioners());
        return "patients/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("patient") PatientDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("doctors", doctorService.getGeneralPractitioners());
            return "patients/form";
        }
        patientService.createPatient(dto);
        return "redirect:/patients";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("doctors", doctorService.getGeneralPractitioners());
        return "patients/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("patient") PatientDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("doctors", doctorService.getGeneralPractitioners());
            return "patients/form";
        }
        patientService.updatePatient(id, dto);
        return "redirect:/patients";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }
}
