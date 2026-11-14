package com.example.csb869_medicinsko_dosie.web.view;

import com.example.csb869_medicinsko_dosie.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
public class StatisticsViewController {

    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final ExaminationService examinationService;
    private final SickLeaveService sickLeaveService;
    private final PatientService patientService;

    @GetMapping
    public String statistics(
            @RequestParam(required = false) Long diagnosisId,
            @RequestParam(required = false) Long gpDoctorId,
            @RequestParam(required = false) Long examDoctorId,
            @RequestParam(required = false) Long historyPatientId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            Model model) {

        // Static stats always shown
        model.addAttribute("mostCommonDiagnosis", diagnosisService.getMostCommonDiagnosis());
        model.addAttribute("gpsWithPatientCount", doctorService.getGPsWithPatientCount());
        model.addAttribute("totalPaidByPatients", examinationService.getTotalPricePaidByPatients());
        model.addAttribute("paidByPatientsPerDoctor", examinationService.getPricePaidByPatientsPerDoctor());
        model.addAttribute("visitsPerDoctor", examinationService.getVisitCountPerDoctor());
        model.addAttribute("monthWithMostSickLeaves", sickLeaveService.getMonthWithMostSickLeaves());
        model.addAttribute("doctorsWithMostSickLeaves", sickLeaveService.getDoctorsWithMostSickLeaves());

        // Dropdown data for filters
        model.addAttribute("allDiagnoses", diagnosisService.getAllDiagnoses());
        model.addAttribute("allGPs", doctorService.getGeneralPractitioners());
        model.addAttribute("allDoctors", doctorService.getAllDoctors());
        model.addAttribute("allPatients", patientService.getAllPatients());

        // Filter params for keeping form state
        model.addAttribute("diagnosisId", diagnosisId);
        model.addAttribute("gpDoctorId", gpDoctorId);
        model.addAttribute("examDoctorId", examDoctorId);
        model.addAttribute("historyPatientId", historyPatientId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        // Dynamic filtered results
        if (diagnosisId != null) {
            model.addAttribute("patientsByDiagnosis", patientService.getPatientsByDiagnosis(diagnosisId));
        }
        if (gpDoctorId != null) {
            model.addAttribute("patientsByGP", patientService.getPatientsByGeneralPractitioner(gpDoctorId));
        }
        if (historyPatientId != null) {
            model.addAttribute("patientHistory", examinationService.getExaminationsByPatient(historyPatientId));
        }
        if (from != null && to != null) {
            model.addAttribute("examinationsByPeriod",
                    examinationService.getExaminationsByDoctorAndPeriod(examDoctorId, from, to));
        }

        return "statistics";
    }
}
