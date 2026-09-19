package com.digitalhub.service;

import com.digitalhub.model.Category;
import com.digitalhub.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}