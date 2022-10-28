package com.tweats.tweats.image;

import com.tweats.tweats.exceptions.ImageNotFoundException;
import com.tweats.tweats.exceptions.NotAnImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image save(MultipartFile file) throws IOException, NotAnImageException {
        if (file.getContentType() == MediaType.IMAGE_PNG_VALUE || file.getContentType() == MediaType.IMAGE_JPEG_VALUE) {
            Image image = new Image(file.getName(), file.getContentType(), file.getBytes(), file.getSize());
            imageRepository.save(image);
            return image;
        }
        throw new NotAnImageException();
    }

    public Image getImage(String imageId) throws ImageNotFoundException {
        return imageRepository.findById(imageId).orElseThrow(() ->  new ImageNotFoundException());
    }



//    public MultipartFile fetch(org.springframework.mock.web.MockMultipartFile mockMultipartFile) {
//    }
}
