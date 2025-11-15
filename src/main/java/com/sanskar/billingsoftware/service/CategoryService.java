package com.sanskar.billingsoftware.service;

import com.sanskar.billingsoftware.io.CategoryRequest;
import com.sanskar.billingsoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryResponse addCategory(CategoryRequest categoryRequest, MultipartFile file) throws IOException;

    List<CategoryResponse> getAllCategories();

    void deleteCategory(String categoryId) throws IOException;
}
