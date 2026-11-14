package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.dto.DoctorDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorViewController {

    private final DoctorService doctorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctors/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("doctor", new DoctorDTO());
        return "doctors/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("doctor") DoctorDTO dto, BindingResult br) {
        if (br.hasErrors()) return "doctors/form";
        doctorService.createDoctor(dto);
        return "redirect:/doctors";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctors/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("doctor") DoctorDTO dto, BindingResult br) {
        if (br.hasErrors()) return "doctors/form";
        doctorService.updateDoctor(id, dto);
        return "redirect:/doctors";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }
}
