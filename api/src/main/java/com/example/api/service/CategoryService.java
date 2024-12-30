package com.example.api.service;

import com.example.api.dto.request.CategoryRequest;
import com.example.api.entity.Category;
import com.example.api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category create(String name){
        Category category = new Category();
        category.setName(name);

        return categoryRepository.save(category);
    }

    public Category get(Long id){
        return categoryRepository.findById(id).get();
    }

    public List<Category> list(){
        return categoryRepository.findAll();
    }

    public void delete(Long id){
        categoryRepository.deleteById(id);
    }

    public Category update(Long id, CategoryRequest request){
        Category category = get(id);
        if(category!=null){
            category.setName(request.getName());
            return categoryRepository.save(category);
        }
        return null;
    }
}
