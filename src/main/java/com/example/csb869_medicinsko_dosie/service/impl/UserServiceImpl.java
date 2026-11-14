package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.*;
import com.example.csb869_medicinsko_dosie.data.repo.*;
import com.example.csb869_medicinsko_dosie.dto.PublicRegisterDTO;
import com.example.csb869_medicinsko_dosie.dto.RegisterDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public void register(RegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Потребителското име вече съществува");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        String roleName = (dto.getRole() != null && !dto.getRole().isBlank()) ? dto.getRole().toUpperCase() : "PATIENT";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        if ("DOCTOR".equals(roleName) && dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            user.setDoctor(doctor);
        } else if ("PATIENT".equals(roleName) && dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            user.setPatient(patient);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void registerPublic(PublicRegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Потребителското име вече съществува");
        }

        String roleName = ("DOCTOR".equalsIgnoreCase(dto.getRole())) ? "DOCTOR" : "PATIENT";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(new HashSet<>(Set.of(role)));

        if ("PATIENT".equals(roleName)) {
            Patient patient = new Patient();
            patient.setName(dto.getName());
            patient.setEgn(dto.getEgn());
            patient.setHealthInsured(false);
            if (dto.getGeneralPractitionerId() != null) {
                Doctor gp = doctorRepository.findById(dto.getGeneralPractitionerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Лекарят не е намерен"));
                patient.setGeneralPractitioner(gp);
            }
            patient = patientRepository.save(patient);
            user.setPatient(patient);
        } else {
            // DOCTOR — link to existing doctor profile or create new
            if (dto.getExistingDoctorId() != null) {
                Doctor doctor = doctorRepository.findById(dto.getExistingDoctorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Лекарят не е намерен"));
                user.setDoctor(doctor);
            } else {
                Doctor doctor = new Doctor();
                doctor.setName(dto.getName());
                doctor.setIdentificationNumber(dto.getIdentificationNumber() != null ? dto.getIdentificationNumber() : "");
                doctor.setSpeciality(dto.getSpeciality() != null ? dto.getSpeciality() : "");
                doctor.setGeneralPractitioner(false);
                doctor = doctorRepository.save(doctor);
                user.setDoctor(doctor);
            }
        }

        userRepository.save(user);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
