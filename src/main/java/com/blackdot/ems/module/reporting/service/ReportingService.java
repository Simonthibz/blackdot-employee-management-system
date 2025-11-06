package com.blackdot.ems.module.reporting.service;

import com.blackdot.ems.module.assessment.repository.AssessmentRepository;
import com.blackdot.ems.module.assessment.repository.AssessmentResultRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.task.repository.TaskRepository;
import com.blackdot.ems.shared.entity.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentResultRepository assessmentResultRepository;

    @Autowired
    private TaskRepository taskRepository;

    public byte[] generateEmployeeReport(String format) throws Exception {
        List<User> employees = userRepository.findByIsActiveTrue();
        
        List<Map<String, Object>> reportData = employees.stream().map(emp -> {
            Map<String, Object> row = new HashMap<>();
            row.put("employeeId", emp.getEmployeeId());
            row.put("firstName", emp.getFirstName());
            row.put("lastName", emp.getLastName());
            row.put("username", emp.getUsername());
            row.put("email", emp.getEmail());
            row.put("department", emp.getDepartment());
            row.put("position", emp.getPosition());
            row.put("roles", emp.getRoles().stream()
                    .map(role -> role.getName().name().replace("ROLE_", ""))
                    .collect(Collectors.joining(", ")));
            row.put("createdAt", emp.getCreatedAt());
            row.put("isActive", emp.getIsActive());
            return row;
        }).collect(Collectors.toList());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Employee Report");
        parameters.put("generatedDate", LocalDateTime.now());
        parameters.put("totalEmployees", employees.size());

        return generateReport("employee-report.jrxml", reportData, parameters, format);
    }

    public byte[] generateQuarterlyAssessmentReport(Quarter quarter, Integer year, String format) throws Exception {
        List<AssessmentResult> results = assessmentResultRepository.findByQuarterAndYear(quarter, year);
        
        List<Map<String, Object>> reportData = results.stream().map(result -> {
            Map<String, Object> row = new HashMap<>();
            row.put("employeeId", result.getUser().getEmployeeId());
            row.put("employeeName", result.getUser().getFirstName() + " " + result.getUser().getLastName());
            row.put("assessmentTitle", result.getAssessment().getTitle());
            row.put("score", result.getScore());
            row.put("totalQuestions", result.getTotalQuestions());
            row.put("correctAnswers", result.getCorrectAnswers());
            row.put("timeTaken", result.getTimeTakenMinutes());
            row.put("passed", result.getPassed());
            row.put("startedAt", result.getStartedAt());
            row.put("completedAt", result.getCompletedAt());
            row.put("quarter", result.getQuarter());
            row.put("year", result.getYear());
            return row;
        }).collect(Collectors.toList());

        // Calculate statistics
        long totalAttempts = results.size();
        long passedAttempts = results.stream().filter(AssessmentResult::getPassed).count();
        double passRate = totalAttempts > 0 ? (double) passedAttempts / totalAttempts * 100 : 0;
        double averageScore = results.stream().mapToInt(AssessmentResult::getScore).average().orElse(0);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", quarter + " " + year + " Assessment Report");
        parameters.put("generatedDate", LocalDateTime.now());
        parameters.put("quarter", quarter.name());
        parameters.put("year", year);
        parameters.put("totalAttempts", totalAttempts);
        parameters.put("passedAttempts", passedAttempts);
        parameters.put("failedAttempts", totalAttempts - passedAttempts);
        parameters.put("passRate", Math.round(passRate * 100.0) / 100.0);
        parameters.put("averageScore", Math.round(averageScore * 100.0) / 100.0);

        return generateReport("quarterly-assessment-report.jrxml", reportData, parameters, format);
    }

    public byte[] generateEmployeeAssessmentHistory(String employeeId, String format) throws Exception {
        // Try to find by employee ID string first (e.g., "EMP005")
        User employee = userRepository.findByEmployeeId(employeeId).orElse(null);
        
        if (employee == null) {
            // Try to parse as numeric database ID and find by ID
            try {
                Long numericId = Long.parseLong(employeeId);
                employee = userRepository.findById(numericId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Employee not found with ID: " + employeeId);
            }
        }
        
        List<AssessmentResult> results = assessmentResultRepository.findByUserIdOrderByCreatedAtDesc(employee.getId());
        
        List<Map<String, Object>> reportData = results.stream().map(result -> {
            Map<String, Object> row = new HashMap<>();
            row.put("assessmentTitle", result.getAssessment().getTitle());
            row.put("quarter", result.getQuarter() != null ? result.getQuarter().name() : "N/A");
            row.put("year", result.getYear() != null ? result.getYear() : 0);
            row.put("score", result.getScore());
            row.put("totalQuestions", result.getTotalQuestions());
            row.put("correctAnswers", result.getCorrectAnswers());
            row.put("timeTaken", result.getTimeTakenMinutes());
            row.put("passed", result.getPassed() ? "Yes" : "No");
            row.put("startedAt", result.getStartedAt());
            row.put("completedAt", result.getCompletedAt());
            return row;
        }).collect(Collectors.toList());

        // Calculate employee statistics
        long totalAttempts = results.size();
        long passedAttempts = results.stream().filter(AssessmentResult::getPassed).count();
        double passRate = totalAttempts > 0 ? (double) passedAttempts / totalAttempts * 100 : 0;
        double averageScore = results.stream()
                .filter(r -> r.getScore() != null)
                .mapToInt(AssessmentResult::getScore)
                .average()
                .orElse(0);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Assessment History - " + employee.getFirstName() + " " + employee.getLastName());
        parameters.put("generatedDate", LocalDateTime.now());
        parameters.put("employeeId", employee.getEmployeeId());
        parameters.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
        parameters.put("department", employee.getDepartment() != null ? employee.getDepartment() : "N/A");
        parameters.put("position", employee.getPosition() != null ? employee.getPosition() : "N/A");
        parameters.put("totalAttempts", totalAttempts);
        parameters.put("passedAttempts", passedAttempts);
        parameters.put("failedAttempts", totalAttempts - passedAttempts);
        parameters.put("passRate", Math.round(passRate * 100.0) / 100.0);
        parameters.put("averageScore", Math.round(averageScore * 100.0) / 100.0);

        return generateReport("employee-assessment-history.jrxml", reportData, parameters, format);
    }

    public Map<String, Object> getAssessmentAnalytics(Quarter quarter, Integer year) {
        List<AssessmentResult> results = assessmentResultRepository.findByQuarterAndYear(quarter, year);
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic statistics
        long totalAttempts = results.size();
        long passedAttempts = results.stream().filter(AssessmentResult::getPassed).count();
        long failedAttempts = totalAttempts - passedAttempts;
        double passRate = totalAttempts > 0 ? (double) passedAttempts / totalAttempts * 100 : 0;
        double averageScore = results.stream().mapToInt(AssessmentResult::getScore).average().orElse(0);
        
        analytics.put("totalAttempts", totalAttempts);
        analytics.put("passedAttempts", passedAttempts);
        analytics.put("failedAttempts", failedAttempts);
        analytics.put("passRate", Math.round(passRate * 100.0) / 100.0);
        analytics.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
        
        // Score distribution
        Map<String, Long> scoreRanges = new HashMap<>();
        scoreRanges.put("90-100", results.stream().filter(r -> r.getScore() >= 90).count());
        scoreRanges.put("80-89", results.stream().filter(r -> r.getScore() >= 80 && r.getScore() < 90).count());
        scoreRanges.put("70-79", results.stream().filter(r -> r.getScore() >= 70 && r.getScore() < 80).count());
        scoreRanges.put("60-69", results.stream().filter(r -> r.getScore() >= 60 && r.getScore() < 70).count());
        scoreRanges.put("Below 60", results.stream().filter(r -> r.getScore() < 60).count());
        analytics.put("scoreDistribution", scoreRanges);
        
        // Department performance
        Map<String, Double> departmentPerformance = results.stream()
                .filter(r -> r.getUser().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getUser().getDepartment(),
                        Collectors.averagingInt(AssessmentResult::getScore)
                ));
        analytics.put("departmentPerformance", departmentPerformance);
        
        // Assessment performance
        Map<String, Double> assessmentPerformance = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getAssessment().getTitle(),
                        Collectors.averagingInt(AssessmentResult::getScore)
                ));
        analytics.put("assessmentPerformance", assessmentPerformance);
        
        return analytics;
    }

    public byte[] generateTaskReport(String format) throws Exception {
        List<Task> tasks = taskRepository.findAll();
        
        List<Map<String, Object>> reportData = tasks.stream().map(task -> {
            Map<String, Object> row = new HashMap<>();
            row.put("title", task.getTitle());
            row.put("description", task.getDescription());
            row.put("assignedToName", task.getAssignedTo() != null ? 
                    task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName() : "Unassigned");
            row.put("assignedByName", task.getAssignedBy() != null ? 
                    task.getAssignedBy().getFirstName() + " " + task.getAssignedBy().getLastName() : "N/A");
            row.put("priority", task.getPriority().name());
            row.put("status", task.getStatus().name());
            row.put("dueDate", task.getDueDate() != null ? task.getDueDate().toLocalDate() : null);
            row.put("createdAt", task.getCreatedAt());
            return row;
        }).collect(Collectors.toList());

        // Calculate statistics
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long pendingTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Task Report - All Tasks");
        parameters.put("generatedDate", LocalDateTime.now());
        parameters.put("totalTasks", totalTasks);
        parameters.put("completedTasks", completedTasks);
        parameters.put("inProgressTasks", inProgressTasks);
        parameters.put("pendingTasks", pendingTasks);

        return generateReport("task-report.jrxml", reportData, parameters, format);
    }

    public byte[] generateEmployeeTaskHistory(String employeeId, String format) throws Exception {
        // Try to find by employee ID string first (e.g., "EMP005")
        User employee = userRepository.findByEmployeeId(employeeId).orElse(null);
        
        if (employee == null) {
            // Try to parse as numeric database ID and find by ID
            try {
                Long numericId = Long.parseLong(employeeId);
                employee = userRepository.findById(numericId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Employee not found with ID: " + employeeId);
            }
        }
        
        List<Task> tasks = taskRepository.findByAssignedTo(employee);
        
        List<Map<String, Object>> reportData = tasks.stream().map(task -> {
            Map<String, Object> row = new HashMap<>();
            row.put("title", task.getTitle());
            row.put("description", task.getDescription());
            row.put("assignedByName", task.getAssignedBy() != null ? 
                    task.getAssignedBy().getFirstName() + " " + task.getAssignedBy().getLastName() : "N/A");
            row.put("priority", task.getPriority().name());
            row.put("status", task.getStatus().name());
            row.put("dueDate", task.getDueDate() != null ? task.getDueDate().toLocalDate() : null);
            row.put("completedAt", task.getCompletedAt());
            row.put("createdAt", task.getCreatedAt());
            return row;
        }).collect(Collectors.toList());

        // Calculate employee task statistics
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long pendingTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "Task History - " + employee.getFirstName() + " " + employee.getLastName());
        parameters.put("generatedDate", LocalDateTime.now());
        parameters.put("employeeId", employee.getEmployeeId());
        parameters.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
        parameters.put("department", employee.getDepartment() != null ? employee.getDepartment() : "N/A");
        parameters.put("position", employee.getPosition() != null ? employee.getPosition() : "N/A");
        parameters.put("totalTasks", totalTasks);
        parameters.put("completedTasks", completedTasks);
        parameters.put("inProgressTasks", inProgressTasks);
        parameters.put("pendingTasks", pendingTasks);
        parameters.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        return generateReport("employee-task-history.jrxml", reportData, parameters, format);
    }

    public Map<String, Object> getTaskAnalytics() {
        List<Task> allTasks = taskRepository.findAll();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic statistics
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgressTasks = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long pendingTasks = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        analytics.put("totalTasks", totalTasks);
        analytics.put("completedTasks", completedTasks);
        analytics.put("inProgressTasks", inProgressTasks);
        analytics.put("pendingTasks", pendingTasks);
        analytics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // Priority distribution
        Map<String, Long> priorityDistribution = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));
        analytics.put("priorityDistribution", priorityDistribution);
        
        // Status distribution
        Map<String, Long> statusDistribution = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));
        analytics.put("statusDistribution", statusDistribution);
        
        // Department task distribution
        Map<String, Long> departmentDistribution = allTasks.stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAssignedTo().getDepartment(),
                        Collectors.counting()
                ));
        analytics.put("departmentDistribution", departmentDistribution);
        
        return analytics;
    }

    private byte[] generateReport(String templateName, List<Map<String, Object>> data, 
                                 Map<String, Object> parameters, String format) throws Exception {
        // Load report template
        ClassPathResource resource = new ClassPathResource("reports/" + templateName);
        InputStream reportTemplate = resource.getInputStream();
        
        JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate);
        
        // Create data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        
        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        // Export to desired format
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        switch (format.toLowerCase()) {
            case "pdf":
                JRPdfExporter pdfExporter = new JRPdfExporter();
                pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                pdfExporter.setConfiguration(new SimplePdfExporterConfiguration());
                pdfExporter.exportReport();
                break;
                
            case "excel":
            case "xls":
                JRXlsExporter xlsExporter = new JRXlsExporter();
                xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                xlsExporter.setConfiguration(new SimpleXlsReportConfiguration());
                xlsExporter.exportReport();
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
        
        return outputStream.toByteArray();
    }
}