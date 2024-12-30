package com.example.api.service;

import com.example.api.dto.request.DessertRequest;
import com.example.api.entity.Category;
import com.example.api.entity.Dessert;
import com.example.api.repository.CategoryRepository;
import com.example.api.repository.DessertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class DessertService {
    @Autowired
    private DessertRepository dessertRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Dessert create(String name, String image,Double price, Long categoryId){
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()){
            throw new RuntimeException("Category not found");
        }
        Dessert dessert = new Dessert();
        dessert.setName(name);
        dessert.setPrice(price);
        dessert.setImage(image);
        dessert.setCategory(category.get());
        return dessertRepository.save(dessert);
    }

    public Dessert get(Long id){
        return dessertRepository.findById(id).get();
    }

    public List<Dessert> list(){
        return dessertRepository.findAll();
    }

    public void delete(Long id){
        dessertRepository.deleteById(id);
    }

    public Dessert update(Long id,String name, String image,Double price, Long categoryId){
        Dessert dessert = get(id);
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()){
            throw new RuntimeException("Category not found");
        }
        if(dessert!=null){
            if(price!=null)
                dessert.setPrice(price);
            if(name!=null)
                dessert.setName(name);
            if(image!=null)
                dessert.setImage(image);
            if(category!=null)
                dessert.setCategory(category.get());
            return dessertRepository.save(dessert);
        }
        return null;
    }
    // Tìm kiếm theo tên món tráng miệng
    public List<Dessert> searchByName(String name) {
        return dessertRepository.findByNameContainingIgnoreCase(name);  // Phương thức này bạn cần phải định nghĩa trong DessertRepository
    }

    // Sắp xếp theo category
    public List<Dessert> sortByCategory(Long categoryId) {
        if (categoryId != null) {
            return dessertRepository.findByCategoryId(categoryId);  // Phương thức này bạn cần phải định nghĩa trong DessertRepository
        }
        return dessertRepository.findAll();  // Trả về tất cả nếu không có categoryId
    }
}
