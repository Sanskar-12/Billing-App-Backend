package com.sanskar.billingsoftware.service.impl;

import com.sanskar.billingsoftware.entity.CategoryEntity;
import com.sanskar.billingsoftware.io.CategoryRequest;
import com.sanskar.billingsoftware.io.CategoryResponse;
import com.sanskar.billingsoftware.respository.CategoryRepository;
import com.sanskar.billingsoftware.respository.ItemRepository;
import com.sanskar.billingsoftware.service.CategoryService;
import com.sanskar.billingsoftware.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public CategoryResponse addCategory(CategoryRequest categoryRequest, MultipartFile file) throws IOException {

        CategoryEntity newCategory = convertToEntity(categoryRequest);

        // upload img to cloudinary
        String imgUrl = cloudinaryService.uploadFile(file);
        newCategory.setImgUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);

        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map((this::convertToResponse)).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(String categoryId) throws IOException {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId).orElseThrow(() -> new RuntimeException("Category not found : " + categoryId));

        // delete from cloudinary
        if (existingCategory.getImgUrl() != null) {
            cloudinaryService.deleteFile(existingCategory.getImgUrl());
        }

        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {

        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());

        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .items(itemsCount)
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest categoryRequest) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .bgColor(categoryRequest.getBgColor())
                .build();
    }
}
