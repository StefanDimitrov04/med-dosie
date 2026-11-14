package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.service.DiagnosisService;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import com.example.csb869_medicinsko_dosie.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
public class StatisticsViewController {

    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final ExaminationService examinationService;
    private final SickLeaveService sickLeaveService;

    @GetMapping
    public String statistics(Model model) {
        model.addAttribute("mostCommonDiagnosis", diagnosisService.getMostCommonDiagnosis());
        model.addAttribute("gpsWithPatientCount", doctorService.getGPsWithPatientCount());
        model.addAttribute("totalPaidByPatients", examinationService.getTotalPricePaidByPatients());
        model.addAttribute("paidByPatientsPerDoctor", examinationService.getPricePaidByPatientsPerDoctor());
        model.addAttribute("visitsPerDoctor", examinationService.getVisitCountPerDoctor());
        model.addAttribute("monthWithMostSickLeaves", sickLeaveService.getMonthWithMostSickLeaves());
        model.addAttribute("doctorsWithMostSickLeaves", sickLeaveService.getDoctorsWithMostSickLeaves());
        return "statistics";
    }
}
