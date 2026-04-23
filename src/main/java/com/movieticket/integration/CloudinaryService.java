package com.movieticket.integration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.movieticket.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final String DEFAULT_FOLDER = "movie_posters";

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(
                originalFilename != null ? originalFilename : ""
        ).toLowerCase();

        if (!isAllowedImageExtension(ext)) {
            throw new BadRequestException("Unsupported file type: " + ext);
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", DEFAULT_FOLDER,
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            "overwrite", false
                    )
            );

            String url = (String) result.get("secure_url");
            log.info("Cloudinary upload success: {}", url);
            return url;

        } catch (IOException ex) {
            log.error("Cloudinary upload failed", ex);
            throw new IOException("Cloudinary upload failed", ex);
        }
    }

    public String uploadFromUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BadRequestException("Image URL is required");
        }

        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw new BadRequestException("Invalid image URL");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    imageUrl,
                    ObjectUtils.asMap(
                            "folder", DEFAULT_FOLDER,
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            "overwrite", false
                    )
            );

            String url = (String) result.get("secure_url");
            log.info("Cloudinary URL upload success: {}", url);
            return url;

        } catch (IOException ex) {
            log.error("Cloudinary URL upload failed", ex);
            throw new IOException("Cloudinary URL upload failed", ex);
        }
    }

    private boolean isAllowedImageExtension(String ext) {
        return ext != null && switch (ext) {
            case "jpg", "jpeg", "png", "gif", "webp", "bmp" -> true;
            default -> false;
        };
    }
}
