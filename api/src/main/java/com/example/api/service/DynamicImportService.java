package com.example.api.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class DynamicImportService {

    @Autowired
    private ApplicationContext applicationContext;  // Inject ApplicationContext để lấy bean repository

    // Phương thức để import dữ liệu từ file Excel
    public void importFromExcel(MultipartFile file, String entityName) throws Exception {

        Class<?> entityClass = Class.forName("com.example.api.entity." + entityName);
        List<Object> entityList = new ArrayList<>();


        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);


        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new Exception("Header row is missing");
        }

        Map<String, Integer> columnIndexMap = new HashMap<>();

        // Duyệt qua các cột của dòng tiêu đề
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            String columnName = headerRow.getCell(i).getStringCellValue();
            columnIndexMap.put(columnName, i);
        }

        // Duyệt qua các dòng dữ liệu (từ dòng 2 trở đi)
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            // Kiểm tra nếu row là null (có thể có các dòng trống trong file Excel)
            if (row == null) {
                continue;  // Bỏ qua dòng trống
            }

            Object entityInstance = entityClass.getDeclaredConstructor().newInstance();

            // Duyệt qua các cột và ánh xạ vào các trường của entity
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (columnIndexMap.containsKey(fieldName)) {
                    int columnIndex = columnIndexMap.get(fieldName);
                    Cell cell = row.getCell(columnIndex);
                    setFieldValue(entityInstance, field, cell);
                }
            }

            entityList.add(entityInstance);
        }

        workbook.close();

        // Lấy repository tương ứng từ ApplicationContext
        String repositoryName = entityName.toLowerCase() + "Repository";  // Tạo tên repository tương ứng với chữ thường
        Object repository = applicationContext.getBean(repositoryName);

        if (repository != null) {
            // Kiểm tra nếu repository là instance của JpaRepository
            if (repository instanceof org.springframework.data.jpa.repository.JpaRepository) {
                org.springframework.data.jpa.repository.JpaRepository<Object, Long> jpaRepository =
                        (org.springframework.data.jpa.repository.JpaRepository<Object, Long>) repository;
                // Lưu dữ liệu vào cơ sở dữ liệu
                jpaRepository.saveAll(entityList);
            } else {
                throw new IllegalArgumentException("Repository không hợp lệ cho entity: " + entityName);
            }
        } else {
            throw new IllegalArgumentException("Không tìm thấy repository cho entity: " + entityName);
        }
    }

    // Phương thức để set giá trị cho trường entity từ cell
    private void setFieldValue(Object entityInstance, Field field, Cell cell) throws IllegalAccessException {
        if (cell == null) return;

        switch (cell.getCellType()) {
            case STRING:
                field.set(entityInstance, cell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    field.set(entityInstance, cell.getDateCellValue());
                } else {
                    // Nếu trường là Long, chuyển đổi từ Double sang Long
                    if (field.getType().equals(Long.class)) {
                        field.set(entityInstance, Math.round(cell.getNumericCellValue()));  // Chuyển giá trị sang Long
                    } else {
                        field.set(entityInstance, cell.getNumericCellValue());  // Nếu không phải Long, giữ nguyên kiểu Double
                    }
                }
                break;
            case BOOLEAN:
                field.set(entityInstance, cell.getBooleanCellValue());
                break;
            default:
                break;
        }
    }
}
