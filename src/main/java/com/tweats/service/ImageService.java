package com.tweats.service;

import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image save(MultipartFile file) throws IOException, NotAnImageException {
        if (Objects.equals(file.getContentType(), MediaType.IMAGE_PNG_VALUE) || file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {

            Image image = new Image(StringUtils.cleanPath(file.getOriginalFilename()), file.getContentType(), file.getBytes(), file.getSize());

            return imageRepository.save(image);
        }
        throw new NotAnImageException();
    }

    public Optional<Image> getImage(String imageId) {
        return imageRepository.findById(imageId);
    }

}
