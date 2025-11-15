package com.sanskar.billingsoftware.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sanskar.billingsoftware.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String imageUrl) throws IOException {
        try {
            String publicId = extractPublicId(imageUrl);

            Map result = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "image"));

            if (result.get("result").equals("ok")) {
                System.out.println("Image deleted successfully: " + publicId);
            } else {
                System.out.println("Failed to delete image: " + publicId + " | Response: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
            throw new IOException("Could not delete image from Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        String withoutVersion = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
        withoutVersion = withoutVersion.substring(withoutVersion.indexOf("/") + 1);

        int dotIndex = withoutVersion.lastIndexOf(".");
        return (dotIndex != -1) ? withoutVersion.substring(0, dotIndex) : withoutVersion;
    }
}
