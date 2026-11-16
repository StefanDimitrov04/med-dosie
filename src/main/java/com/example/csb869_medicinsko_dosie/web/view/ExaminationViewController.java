package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.data.repo.UserRepository;
import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;
import com.example.csb869_medicinsko_dosie.service.DiagnosisService;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import com.example.csb869_medicinsko_dosie.service.PatientService;
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
@RequestMapping("/examinations")
@RequiredArgsConstructor
public class ExaminationViewController {

    private final ExaminationService examinationService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model, Authentication auth) {
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
        boolean isDoctor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        List<ExaminationDTO> exams;
        if (isPatient) {
            Long patientId = userRepository.findByUsername(auth.getName())
                    .map(u -> u.getPatient() != null ? u.getPatient().getId() : null)
                    .orElse(null);
            exams = patientId != null
                    ? examinationService.getExaminationsByPatient(patientId)
                    : List.of();
        } else if (isDoctor) {
            Long doctorId = userRepository.findByUsername(auth.getName())
                    .map(u -> u.getDoctor() != null ? u.getDoctor().getId() : null)
                    .orElse(null);
            exams = doctorId != null
                    ? examinationService.getExaminationsByDoctor(doctorId)
                    : examinationService.getAllExaminations();
        } else {
            exams = examinationService.getAllExaminations();
        }
        model.addAttribute("examinations", exams);
        return "examinations/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("examination", examinationService.getExaminationById(id));
        return "examinations/detail";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/new")
    public String newForm(Model model, Authentication auth) {
        ExaminationDTO dto = new ExaminationDTO();
        boolean isDoctor = isDoctor(auth);
        if (isDoctor) {
            Long doctorId = getDoctorId(auth);
            dto.setDoctorId(doctorId);
            model.addAttribute("currentDoctor", doctorId != null ? doctorService.getDoctorById(doctorId) : null);
        } else {
            model.addAttribute("doctors", doctorService.getAllDoctors());
        }
        model.addAttribute("examination", dto);
        model.addAttribute("isDoctor", isDoctor);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
        return "examinations/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("examination") ExaminationDTO dto, BindingResult br,
                         Model model, Authentication auth) {
        boolean isDoctor = isDoctor(auth);
        if (isDoctor) {
            dto.setDoctorId(getDoctorId(auth));
        }
        if (br.hasErrors()) {
            if (isDoctor) {
                Long doctorId = getDoctorId(auth);
                model.addAttribute("currentDoctor", doctorId != null ? doctorService.getDoctorById(doctorId) : null);
            } else {
                model.addAttribute("doctors", doctorService.getAllDoctors());
            }
            model.addAttribute("isDoctor", isDoctor);
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
            return "examinations/form";
        }
        examinationService.createExamination(dto);
        return "redirect:/examinations";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        boolean isDoctor = isDoctor(auth);
        if (isDoctor) {
            Long doctorId = getDoctorId(auth);
            model.addAttribute("currentDoctor", doctorId != null ? doctorService.getDoctorById(doctorId) : null);
        } else {
            model.addAttribute("doctors", doctorService.getAllDoctors());
        }
        model.addAttribute("examination", examinationService.getExaminationById(id));
        model.addAttribute("isDoctor", isDoctor);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
        return "examinations/form";
    }

    @Secured({"ROLE_DOCTOR", "ROLE_ADMIN"})
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("examination") ExaminationDTO dto,
                         BindingResult br, Model model, Authentication auth) {
        boolean isDoctor = isDoctor(auth);
        if (isDoctor) {
            dto.setDoctorId(getDoctorId(auth));
        }
        if (br.hasErrors()) {
            if (isDoctor) {
                Long doctorId = getDoctorId(auth);
                model.addAttribute("currentDoctor", doctorId != null ? doctorService.getDoctorById(doctorId) : null);
            } else {
                model.addAttribute("doctors", doctorService.getAllDoctors());
            }
            model.addAttribute("isDoctor", isDoctor);
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
            return "examinations/form";
        }
        examinationService.updateExamination(id, dto);
        return "redirect:/examinations";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return "redirect:/examinations";
    }

    private boolean isDoctor(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
    }

    private Long getDoctorId(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .map(u -> u.getDoctor() != null ? u.getDoctor().getId() : null)
                .orElse(null);
    }
}
