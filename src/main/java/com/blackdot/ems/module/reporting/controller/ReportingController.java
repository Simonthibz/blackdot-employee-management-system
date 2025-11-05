package com.blackdot.ems.module.reporting.controller;

import com.blackdot.ems.module.reporting.service.ReportingService;
import com.blackdot.ems.shared.entity.Quarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<byte[]> generateEmployeeReport(@RequestParam(defaultValue = "pdf") String format) {
        try {
            byte[] reportBytes = reportingService.generateEmployeeReport(format);
            
            HttpHeaders headers = new HttpHeaders();
            String fileName = "employee-report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            
            if ("pdf".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", fileName + ".pdf");
            } else if ("excel".equalsIgnoreCase(format) || "xls".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName + ".xls");
            }
            
            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/assessments/quarterly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<byte[]> generateQuarterlyAssessmentReport(
            @RequestParam Quarter quarter,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            byte[] reportBytes = reportingService.generateQuarterlyAssessmentReport(quarter, year, format);
            
            HttpHeaders headers = new HttpHeaders();
            String fileName = "assessment-report-" + quarter + "-" + year + "-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            
            if ("pdf".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", fileName + ".pdf");
            } else if ("excel".equalsIgnoreCase(format) || "xls".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName + ".xls");
            }
            
            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employees/{employeeId}/assessments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<byte[]> generateEmployeeAssessmentHistory(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            byte[] reportBytes = reportingService.generateEmployeeAssessmentHistory(employeeId, format);
            
            HttpHeaders headers = new HttpHeaders();
            String fileName = "employee-assessment-history-" + employeeId + "-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            
            if ("pdf".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", fileName + ".pdf");
            } else if ("excel".equalsIgnoreCase(format) || "xls".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName + ".xls");
            }
            
            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/analytics/assessments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getAssessmentAnalytics(
            @RequestParam Quarter quarter,
            @RequestParam Integer year) {
        try {
            Map<String, Object> analytics = reportingService.getAssessmentAnalytics(quarter, year);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}