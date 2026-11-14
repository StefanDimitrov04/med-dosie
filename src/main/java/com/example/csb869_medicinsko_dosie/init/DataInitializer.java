package com.example.csb869_medicinsko_dosie.init;

import com.example.csb869_medicinsko_dosie.data.entity.*;
import com.example.csb869_medicinsko_dosie.data.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        initRoles();
        initAdminUser();
        initSampleData();
    }

    private void initRoles() {
        for (String roleName : new String[]{"ADMIN", "DOCTOR", "PATIENT"}) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }
    }

    private void initAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
        }
    }

    private void initSampleData() {
        if (doctorRepository.count() == 0) {
            Doctor gp = Doctor.builder()
                    .identificationNumber("DOC001")
                    .name("Д-р Иванов")
                    .speciality("Общопрактикуващ лекар")
                    .isGeneralPractitioner(true)
                    .build();
            Doctor specialist = Doctor.builder()
                    .identificationNumber("DOC002")
                    .name("Д-р Петрова")
                    .speciality("Кардиолог")
                    .isGeneralPractitioner(false)
                    .build();
            doctorRepository.save(gp);
            doctorRepository.save(specialist);

            if (patientRepository.count() == 0) {
                Patient patient = Patient.builder()
                        .name("Георги Георгиев")
                        .egn("8501011234")
                        .generalPractitioner(gp)
                        .healthInsured(true)
                        .build();
                patientRepository.save(patient);
            }

            if (userRepository.findByUsername("doctor1").isEmpty()) {
                Role doctorRole = roleRepository.findByName("DOCTOR").orElseThrow();
                User doctorUser = User.builder()
                        .username("doctor1")
                        .password(passwordEncoder.encode("doctor123"))
                        .roles(Set.of(doctorRole))
                        .doctor(gp)
                        .build();
                userRepository.save(doctorUser);
            }
        }
    }
}
