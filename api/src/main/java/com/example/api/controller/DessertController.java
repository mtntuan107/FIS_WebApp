package com.example.api.controller;

import com.example.api.dto.request.DessertRequest;
import com.example.api.entity.Dessert;
import com.example.api.service.CloudinaryService;
import com.example.api.service.DessertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dessert")
@CrossOrigin(origins = "http://localhost:4200")
public class DessertController {
    @Autowired
    private DessertService dessertService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<Dessert> create(@RequestBody DessertRequest request){
        Dessert dessert = dessertService.create(request.getName(), request.getImage(),request.getPrice(), request.getCategoryId());
        return new ResponseEntity<>(dessert, HttpStatus.CREATED);
    }

    @PostMapping("/create2")
    public ResponseEntity<Dessert> create2(@RequestParam String name,
                                           @RequestParam (required = false) MultipartFile image,
                                           @RequestParam Double price,
                                           @RequestParam Long categoryId) throws IOException {
        String img = null;

        // If an image is provided, upload it to Cloudinary
        if (image != null && !image.isEmpty()) {
            img = cloudinaryService.uploadImage(image);
        }

        Dessert dessert = dessertService.create(name, img, price, categoryId);
        return new ResponseEntity<>(dessert, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Dessert>> list(){
        List<Dessert> desserts = dessertService.list();
        return new ResponseEntity<>(desserts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        Dessert dessert = dessertService.get(id);

        if(dessert != null){
            dessertService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dessert> update(@PathVariable Long id,
                                          @RequestParam String name,
                                          @RequestParam(required = false) MultipartFile image,
                                          @RequestParam Double price,
                                          @RequestParam Long categoryId) throws IOException {

        String img = null;

        // If an image is provided, upload it to Cloudinary
        if (image != null && !image.isEmpty()) {
            img = cloudinaryService.uploadImage(image);
        }

        // Call the service layer to update the dessert
        Dessert updatedDessert = dessertService.update(id, name, img, price, categoryId);

        if (updatedDessert != null) {
            return new ResponseEntity<>(updatedDessert, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Dessert>> search(@RequestParam String name) {
        List<Dessert> desserts = dessertService.searchByName(name);
        return new ResponseEntity<>(desserts, HttpStatus.OK);
    }

    // API sắp xếp theo category
    @GetMapping("/sort")
    public ResponseEntity<List<Dessert>> sortByCategory(@RequestParam(required = false) Long categoryId) {
        List<Dessert> desserts = dessertService.sortByCategory(categoryId);
        return new ResponseEntity<>(desserts, HttpStatus.OK);
    }
}
