package com.example.api.controller;

import com.example.api.service.DynamicImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
@CrossOrigin(origins = "http://localhost:4200")
public class ImportController {

    @Autowired
    private DynamicImportService importService;

    @PostMapping("/import-excel")
    public String importData(@RequestParam("file") MultipartFile file, @RequestParam("entityName") String entityName) {
        try {
            importService.importFromExcel(file, entityName);
            return "Import thành công!";
        } catch (Exception e) {
            return "Lỗi khi import dữ liệu: " + e.getMessage();
        }
    }
}

