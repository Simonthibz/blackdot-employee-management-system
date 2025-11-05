package com.blackdot.ems.shared.config;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.authentication.repository.RoleRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_HR));
            roleRepository.save(new Role(ERole.ROLE_DATA_CAPTURER));
            roleRepository.save(new Role(ERole.ROLE_SUPERVISOR));
            roleRepository.save(new Role(ERole.ROLE_EMPLOYEE));
        }

        // Create default admin user
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            User admin = new User("admin", "admin@blackdot.com", 
                    passwordEncoder.encode("admin123"), "System", "Administrator");
            admin.setEmployeeId("EMP001");
            admin.setDepartment("IT");
            admin.setPosition("System Administrator");
            admin.setHireDate(LocalDate.now());
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            // Create HR user
            Role hrRole = roleRepository.findByName(ERole.ROLE_HR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            User hr = new User("hr_manager", "hr@blackdot.com", 
                    passwordEncoder.encode("hr123"), "HR", "Manager");
            hr.setEmployeeId("EMP002");
            hr.setDepartment("Human Resources");
            hr.setPosition("HR Manager");
            hr.setHireDate(LocalDate.now());
            hr.setRoles(Set.of(hrRole));
            userRepository.save(hr);

            // Create sample data capturer
            Role dataCapturerRole = roleRepository.findByName(ERole.ROLE_DATA_CAPTURER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            User dataCapturer = new User("data_capturer1", "datacapturer@blackdot.com", 
                    passwordEncoder.encode("dc123"), "John", "Doe");
            dataCapturer.setEmployeeId("EMP003");
            dataCapturer.setDepartment("Data Management");
            dataCapturer.setPosition("Data Capturer");
            dataCapturer.setHireDate(LocalDate.now());
            dataCapturer.setRoles(Set.of(dataCapturerRole));
            userRepository.save(dataCapturer);
        }

        // Create default assessment for data capturers
        if (assessmentRepository.count() == 0) {
            Assessment dataCapturerAssessment = new Assessment(
                    "Data Capturer Quarterly Assessment", 
                    "Quarterly assessment to evaluate data capturer skills and knowledge"
            );
            dataCapturerAssessment.setPassingScore(75);
            dataCapturerAssessment.setTimeLimitMinutes(45);
            assessmentRepository.save(dataCapturerAssessment);
        }
    }
}