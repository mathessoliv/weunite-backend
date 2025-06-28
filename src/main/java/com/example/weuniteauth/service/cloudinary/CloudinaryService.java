package com.example.weuniteauth.service.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;


@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadPost(MultipartFile file, Long userId) {

        Map<String, Object> options = Map.of(
                "folder", "posts/" + userId,
                "tags", "post, user_content",
                "quality", "auto",
                "resource_type", "auto"
        );

        try {

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }
}


