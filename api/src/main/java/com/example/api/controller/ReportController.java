package com.example.api.controller;

import com.example.api.dto.response.ResponseMessage;
import com.example.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Endpoint để tạo báo cáo từ file .jrxml có sẵn
    @GetMapping("/pdf")
    public void generateExistingReport(@RequestParam String reportName, HttpServletResponse response,@RequestParam String sql) {
        try {
            reportService.generatePdfReport(response, reportName, sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/xlsx")
    public void generateXlsxReport(@RequestParam String reportName, HttpServletResponse response,@RequestParam String sql) {
        try {
            reportService.generateXlsxReport(response, reportName,sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/docx")
    public void generateDocxReport(@RequestParam String reportName, HttpServletResponse response,@RequestParam String sql) {
        try {
            reportService.generateDocxReport(response, reportName,sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/custom")
    public ResponseEntity<ResponseMessage> generateReport(@RequestParam String reportName,
                                                          @RequestParam String sql,
                                                          HttpServletResponse response) {
        try {
            // Gọi phương thức trong service để tạo báo cáo
            boolean result = reportService.createNewReportTemplateDynamic(reportName, sql);

            if (result) {
                ResponseMessage responseMessage = new ResponseMessage("Report template created successfully.", "OK");
                return ResponseEntity.ok(responseMessage);
            } else {
                ResponseMessage responseMessage = new ResponseMessage("Error creating report template.", "ERROR");
                return ResponseEntity.status(500).body(responseMessage);
            }
        } catch (IllegalArgumentException e) {
            ResponseMessage responseMessage = new ResponseMessage("Invalid SQL query: " + e.getMessage(), "ERROR");
            return ResponseEntity.status(400).body(responseMessage);
        } catch (Exception e) {
            ResponseMessage responseMessage = new ResponseMessage("Error generating report: " + e.getMessage(), "ERROR");
            return ResponseEntity.status(500).body(responseMessage);
        }
    }

    @GetMapping("/getColumns")
    public ResponseEntity<List<String>> getColumns(@RequestParam String tableName) {
        try {
            List<String> columns = reportService.getColumnNames(tableName);
            if (columns.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(columns);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFile(@RequestParam String reportName) {
        boolean isExistFile = reportService.doesFileExist(reportName);
        return new ResponseEntity<>(isExistFile, HttpStatus.OK);
    }

    @PostMapping("/read-file-to-pdf")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam("jasperFile") MultipartFile jasperFile) {
        try {
            // Tạo báo cáo PDF từ file .jrxml
            byte[] pdfBytes = reportService.readFileToPdf(jasperFile);

            // Đặt các header cho phản hồi (file PDF)
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=report.pdf");
            headers.add("Content-Type", "application/pdf");

            // Trả về file PDF dưới dạng ResponseEntity
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
