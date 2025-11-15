package com.sanskar.billingsoftware.service;


import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {

    // Upload file
    public String uploadFile(MultipartFile file) throws IOException;

    // Delete file
    public void deleteFile(String publicId) throws IOException;
}
