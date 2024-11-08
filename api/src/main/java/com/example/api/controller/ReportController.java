package com.example.api.controller;

import com.example.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Endpoint để tạo báo cáo từ file .jrxml có sẵn
    @GetMapping("/generate-existing-report")
    public void generateExistingReport(@RequestParam String reportName, HttpServletResponse response) {
        try {
            reportService.generateExistingReport(response, reportName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Endpoint để tạo mới file .jrxml và tạo báo cáo từ file đó
    @GetMapping("/generate-new-report")
    public void generateNewReport(@RequestParam String reportName, HttpServletResponse response) {
        try {
            reportService.createNewReportTemplate(reportName);
            reportService.generateExistingReport(response, reportName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/generate-xlsx-report")
    public void generateXlsxReport(@RequestParam String reportName, HttpServletResponse response) {
        try {
            reportService.generateXlsxReport(response, reportName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
