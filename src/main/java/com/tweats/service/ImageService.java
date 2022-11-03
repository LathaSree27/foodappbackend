package com.tweats.service;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image save(MultipartFile file) throws IOException, NotAnImageException {
        if (isPngFile(file) || isJpegFile(file)){
            Image image = new Image(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())), file.getContentType(), file.getBytes(), file.getSize());
            return imageRepository.save(image);
        }
        throw new NotAnImageException();
    }

    private boolean isJpegFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), MediaType.IMAGE_JPEG_VALUE);
    }

    private boolean isPngFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), MediaType.IMAGE_PNG_VALUE);
    }

    public Image getImage(String imageId) throws ImageNotFoundException {

        Optional<Image> image = imageRepository.findById(imageId);
        if(image.isPresent()) return image.get();
        throw new ImageNotFoundException();

    }

}
