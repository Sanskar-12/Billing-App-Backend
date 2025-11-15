package com.sanskar.billingsoftware.service.impl;

import com.sanskar.billingsoftware.entity.CategoryEntity;
import com.sanskar.billingsoftware.entity.ItemEntity;
import com.sanskar.billingsoftware.io.ItemRequest;
import com.sanskar.billingsoftware.io.ItemResponse;
import com.sanskar.billingsoftware.respository.CategoryRepository;
import com.sanskar.billingsoftware.respository.ItemRepository;
import com.sanskar.billingsoftware.service.CloudinaryService;
import com.sanskar.billingsoftware.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) throws IOException {

        String imgUrl = cloudinaryService.uploadFile(file);
        ItemEntity newItem = convertToEntity(request);
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imgUrl);
        newItem = itemRepository.save(newItem);

        return convertToResponse(newItem);

    }

    private ItemResponse convertToResponse(ItemEntity newItem) {

        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll().stream().map(itemEntity -> convertToResponse(itemEntity)).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(String itemId) throws IOException {
        ItemEntity existingItem = itemRepository.findByItemId(itemId).orElseThrow(() -> new RuntimeException("Item not found" + itemId));

        if (existingItem.getImgUrl() != null) {
            cloudinaryService.deleteFile(existingItem.getImgUrl());
        }

        itemRepository.delete(existingItem);
    }
}
