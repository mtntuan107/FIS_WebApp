package com.example.api.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;
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

    public void generatePdfReport(HttpServletResponse response, String reportName, String sql) throws Exception {
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);

        if (!jrxmlFile.exists()) {
            throw new Exception("File .jrxml không tồn tại tại đường dẫn: " + filePath);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getPath());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Data Report");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + reportName + ".pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    public void generateXlsxReport(HttpServletResponse response, String reportName, String sql) throws Exception {
        // Truy vấn dữ liệu từ cơ sở dữ liệu
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

        // Tạo nguồn dữ liệu cho báo cáo
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        // Đường dẫn đến file .jrxml
        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);
        if (!jrxmlFile.exists()) {
            throw new Exception("File .jrxml không tồn tại tại đường dẫn: " + filePath);
        }

        // Compile file .jrxml thành JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getPath());

        // Đặt các tham số cho báo cáo
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "User Report");

        // Điền dữ liệu vào báo cáo JasperPrint
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Cấu hình đầu ra của file .xlsx
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".xlsx");

        // Thiết lập JRXlsxExporter để xuất báo cáo ra file Excel
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));

        // Cấu hình để tiêu đề chiếm 1 hàng và mỗi dữ liệu nằm trong một ô riêng
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);                    // Không tạo mỗi trang mới trên mỗi sheet
        configuration.setIgnorePageMargins(true);                   // Bỏ qua lề
        configuration.setDetectCellType(true);                      // Tự động xác định kiểu dữ liệu của ô
        configuration.setWhitePageBackground(false);                // Loại bỏ nền trắng để đẹp hơn
        configuration.setRemoveEmptySpaceBetweenRows(true);         // Loại bỏ khoảng trống giữa các hàng
        configuration.setCollapseRowSpan(false);                    // Cho phép merge cell tiêu đề

        exporter.setConfiguration(configuration);
        exporter.exportReport();
    }



    public void generateDocxReport(HttpServletResponse response, String reportName, String sql) throws Exception {
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);

        if (!jrxmlFile.exists()) {
            throw new Exception("File .jrxml không tồn tại tại đường dẫn: " + filePath);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getPath());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "User Report");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".docx");

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));

        DocxExporterConfiguration configuration = new SimpleDocxExporterConfiguration();
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }
    public boolean createNewReportTemplateDynamic(String reportName, String sql) throws Exception {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL không hợp lệ.");
        }

        StringBuilder jrxml = new StringBuilder();
        jrxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        jrxml.append("<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n");
        jrxml.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        jrxml.append("    xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n");
        jrxml.append("    name=\"" + reportName + "\" pageWidth=\"612\" pageHeight=\"792\" columnWidth=\"555\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">\n");

        jrxml.append("    <queryString language=\"SQL\"><![CDATA[" + sql + "]]></queryString>\n");

        Map<Integer, String> typeMapping = Map.of(
                -5, "java.lang.Long",
                12, "java.lang.String",
                4, "java.lang.Integer",
                8, "java.lang.Double",
                3, "java.math.BigDecimal",
                91, "java.sql.Date",
                93, "java.sql.Timestamp",
                16, "java.lang.Boolean"
        );

        ResultSet rs = jdbcTemplate.getDataSource().getConnection().createStatement().executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        int pageWidth = 555;
        int marginLeft = 20;
        int marginRight = 20;
        int totalWidth = pageWidth - marginLeft - marginRight;
        int columnWidth = totalWidth / columnCount;

        for (int i = 1; i <= columnCount; i++) {
            String columnName = rsmd.getColumnLabel(i);
            String javaType = typeMapping.getOrDefault(rsmd.getColumnType(i), "java.lang.String");
            jrxml.append("    <field name=\"" + columnName + "\" class=\"" + javaType + "\"/>\n");
        }

        jrxml.append("    <title>\n");
        jrxml.append("        <band height=\"50\">\n");
        jrxml.append("            <staticText>\n");
        jrxml.append("                <reportElement x=\"0\" y=\"0\" width=\"" + totalWidth + "\" height=\"30\"/>\n");
        jrxml.append("                <textElement><font size=\"20\" isBold=\"true\"/></textElement>\n");
        jrxml.append("                <text><![CDATA[" + reportName + " Report]]></text>\n");
        jrxml.append("            </staticText>\n");
        jrxml.append("        </band>\n");
        jrxml.append("    </title>\n");

        jrxml.append("    <columnHeader>\n");
        jrxml.append("        <band height=\"20\" splitType=\"Stretch\" >\n");

        for (int i = 1; i <= columnCount; i++) {
            int xPosition = (i - 1) * columnWidth;
            jrxml.append("            <staticText>\n");
            jrxml.append("                <reportElement x=\"" + xPosition + "\" y=\"0\" width=\"" + columnWidth + "\" height=\"20\" uuid=\"175a3348-93a2-423b-9d41-968b38bc8fa4\">\n");
            jrxml.append("                      <property name=\"com.jaspersoft.studio.data.sql.tables\" value=\"\"/>\n");
            jrxml.append("                      <property name=\"com.jaspersoft.studio.data.defaultdataadapter\" value=\"Postgres\"/>\n");
            jrxml.append("                </reportElement>\n");
            jrxml.append("                    <box>\n");
            jrxml.append("                        <topPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <leftPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <bottomPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <rightPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                    </box>\n");
            jrxml.append("                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>\n");
            jrxml.append("                <text><![CDATA[" + rsmd.getColumnLabel(i) + "]]></text>\n");
            jrxml.append("            </staticText>\n");
        }
        jrxml.append("        </band>\n");
        jrxml.append("    </columnHeader>\n");

        jrxml.append("    <detail>\n");
        jrxml.append("        <band height=\"20\" splitType=\"Stretch\" >\n");
        jrxml.append("          <property name=\"com.jaspersoft.studio.layout\" value=\"com.jaspersoft.studio.editor.layout.grid.JSSGridBagLayout\"/>\n");
        for (int i = 1; i <= columnCount; i++) {
            int xPosition = (i - 1) * columnWidth;
            jrxml.append("            <textField textAdjust=\"StretchHeight\"> \n");
            jrxml.append("                <reportElement stretchType=\"ElementGroupHeight\" x=\"" + xPosition + "\" y=\"0\" width=\"" + columnWidth + "\" height=\"20\" uuid=\"e6285a83-b120-4c84-a54a-67e96c265538\">\n");
            jrxml.append("                  <property name=\"com.jaspersoft.studio.data.sql.tables\" value=\"\"/>\n");
            jrxml.append("                  <property name=\"com.jaspersoft.studio.data.defaultdataadapter\" value=\"Postgres\"/>\n");
            jrxml.append("                </reportElement>\n");
            jrxml.append("                    <box>\n");
            jrxml.append("                        <topPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <leftPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <bottomPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                        <rightPen lineWidth=\"1.0\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n");
            jrxml.append("                    </box>\n");
            jrxml.append("                <textElement textAlignment=\"Center\">\n");
            jrxml.append("                </textElement>\n");
            jrxml.append("                <textFieldExpression><![CDATA[$F{" + rsmd.getColumnLabel(i) + "}]]></textFieldExpression>\n");
            jrxml.append("            </textField>\n");
        }
        jrxml.append("        </band>\n");
        jrxml.append("    </detail>\n");

        jrxml.append("</jasperReport>");

        String filePath = "src/main/resources/reports/" + reportName + ".jrxml";
        File jrxmlFile = new File(filePath);
        if (!jrxmlFile.exists()) {
            jrxmlFile.getParentFile().mkdirs();
        } else {
            // Xóa nội dung file nếu đã tồn tại
            try (PrintWriter writer = new PrintWriter(jrxmlFile)) {
                writer.print("");
            }
        }
        try (OutputStream os = new FileOutputStream(jrxmlFile)) {
            os.write(jrxml.toString().getBytes());
            System.out.println("Create file [.jrxml] at: " + filePath);
        }
        return true;
    }


    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);


            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }
        }

        return columnNames;
    }

    private final String reportDirectory = "src/main/resources/reports/";

    public boolean doesFileExist(String reportName) {
        File file = new File(reportDirectory + reportName+ ".jrxml");
        return file.exists() && file.isFile();
    }

    public byte[] readFileToPdf(MultipartFile jasperFile) throws JRException, IOException {
        // Tải file .jrxml từ MultipartFile
        InputStream jasperFileInputStream = jasperFile.getInputStream();

        // Biên dịch báo cáo từ file .jrxml
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperFileInputStream);

        // Lấp đầy báo cáo với dữ liệu (ở đây không có tham số nào, chỉ là báo cáo trống)
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new java.util.HashMap<>());

        // Xuất báo cáo dưới dạng byte array (PDF)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

        // Trả về PDF dưới dạng byte array
        return byteArrayOutputStream.toByteArray();
    }

    @Autowired
    private DataSource dataSource;

    public byte[] generatePdfReport(String reportName, Map<String, Object> parameters) {
        try {

            String templatePath = "/reports/" + reportName + ".jrxml";

            InputStream reportStream = getClass().getResourceAsStream(templatePath);
            if (reportStream == null) {
                throw new RuntimeException("Không tìm thấy template báo cáo: " + templatePath);
            }

            // Biên dịch tệp JRXML thành tệp Jasper
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Kết nối database hoặc sử dụng datasource (ví dụ: JDBC hoặc Bean Collection)
            JRDataSource dataSource = new JREmptyDataSource(); // Tạm thời dùng DataSource trống

            // Điền dữ liệu vào báo cáo
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Xuất báo cáo dưới dạng PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo PDF", e);
        }
    }
}
