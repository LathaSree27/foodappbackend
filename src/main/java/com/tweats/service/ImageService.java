package com.tweats.service;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@Setter
public class ImageService {

    private final ImageRepository imageRepository;
    @Value("${image.max-file-size}")
    private long maxSizeOfImage;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image save(MultipartFile file) throws IOException, NotAnImageException, ImageSizeExceededException {
        if ((isPngFile(file) || isJpegFile(file)) && isValidSize(file)) {
            Image image = new Image(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())), file.getContentType(), file.getBytes(), file.getSize());
            return imageRepository.save(image);
        }
        throw new NotAnImageException();
    }

    private boolean isValidSize(MultipartFile file) throws ImageSizeExceededException {
        if (maxSizeOfImage < file.getSize()) throw new ImageSizeExceededException();
        return true;
    }

    private boolean isJpegFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), MediaType.IMAGE_JPEG_VALUE);
    }

    private boolean isPngFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), MediaType.IMAGE_PNG_VALUE);
    }

    public Image getImage(String imageId) throws ImageNotFoundException {

        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isPresent()) return image.get();
        throw new ImageNotFoundException();

    }

}
