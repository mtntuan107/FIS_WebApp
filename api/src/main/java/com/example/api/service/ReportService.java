package com.example.api.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsxExporterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Phương thức để tạo báo cáo từ file .jrxml có sẵn
    public void generateExistingReport(HttpServletResponse response, String reportName) throws Exception {
        // Lấy dữ liệu từ cơ sở dữ liệu (PostgreSQL)
        String sql = "SELECT * FROM users";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

        // Chuyển đổi dữ liệu thành JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        // Đọc file .jrxml có sẵn từ thư mục resources/reports
        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);

        if (!jrxmlFile.exists()) {
            throw new Exception("File .jrxml không tồn tại tại đường dẫn: " + filePath);
        }

        // Biên dịch .jrxml thành JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getPath());

        // Tạo các tham số báo cáo
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "User Report");

        // Điền dữ liệu vào báo cáo
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Thiết lập header cho response và xuất PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + reportName + ".pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    // Phương thức để tạo mới file .jrxml
    public void createNewReportTemplate(String reportName) throws Exception {
        // Tạo nội dung .jrxml (có thể tuỳ chỉnh nội dung theo yêu cầu)
        String jrxmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"" + reportName + "\" pageWidth=\"612\" pageHeight=\"792\" columnWidth=\"555\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">\n" +
                "    <queryString language=\"SQL\">\n" +
                "        <![CDATA[select * from users;]]>\n" +
                "    </queryString>\n" +
                "    <field name=\"id\" class=\"java.lang.Integer\"/>\n" +
                "    <field name=\"email\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"firstname\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"lastname\" class=\"java.lang.String\"/>\n" +
                "    <detail>\n" +
                "        <band height=\"125\" splitType=\"Stretch\"/>\n" +
                "    </detail>\n" +
                "</jasperReport>";

        // Lưu nội dung vào file .jrxml trong thư mục resources/reports
        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);

        if (!jrxmlFile.exists()) {
            try (OutputStream os = new FileOutputStream(jrxmlFile)) {
                os.write(jrxmlContent.getBytes());
            }
        }
    }

    // Phương thức để xuất báo cáo ra file Excel (XLSX)
    public void generateXlsxReport(HttpServletResponse response, String reportName) throws Exception {
        // Lấy dữ liệu từ cơ sở dữ liệu (PostgreSQL)
        String sql = "SELECT * FROM users";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

        // Chuyển đổi dữ liệu thành JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        // Đọc file .jrxml có sẵn từ thư mục resources/reports
        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);

        if (!jrxmlFile.exists()) {
            throw new Exception("File .jrxml không tồn tại tại đường dẫn: " + filePath);
        }

        // Biên dịch .jrxml thành JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getPath());

        // Tạo các tham số báo cáo
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "User Report");

        // Điền dữ liệu vào báo cáo
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Xuất báo cáo dưới dạng XLSX
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".xlsx");

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
        exporter.exportReport();
    }

}
