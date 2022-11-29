package com.tweats.service;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    @Mock
    private ImageRepository imageRepository;
    @InjectMocks
    private ImageService imageService;

    @Test
    void shouldBeAbleToSaveImageFile() throws IOException, NotAnImageException, ImageSizeExceededException {
        long maxImageSize = 1024 * 1024 * 3;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        imageService.setMaxSizeOfImage(maxImageSize);

        imageService.save(mockMultipartFile);

        verify(imageRepository).save(any());

    }

    @Test
    void shouldThrowNotAnImageExceptionWhenTheFileIsNotPngOrJpegFormat() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.APPLICATION_FORM_URLENCODED_VALUE, "hello".getBytes());

        assertThrows(NotAnImageException.class, () -> imageService.save(mockMultipartFile));
    }

    @Test
    void shouldBeAbleToFetchImageWhenImageIdIsGiven() throws ImageNotFoundException {
        String imageId = "Image@123";
        Image image = new Image("image.png",MediaType.IMAGE_JPEG_VALUE,"hello".getBytes(),1L);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        ResponseEntity<byte[]> expectedResponseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"")
                .contentType(MediaType.valueOf(image.getContentType()))
                .body(image.getData());

        ResponseEntity<byte[]> actualImageResponse = imageService.getImageResponse(imageId);

        assertThat(actualImageResponse, is(expectedResponseEntity));
    }

    @Test
    void shouldThrowImageNotFoundExceptionWhenInvalidImageIdIsGiven() {
        String imageId = "Image@123";

        assertThrows(ImageNotFoundException.class, () -> imageService.getImageResponse(imageId));
    }

    @Test
    void shouldThrowImageSizeExceededExceptionWhenImageSizeIsMoreThanConfigured() {
        byte[] content = new byte[1024 * 1024 * 4];
        long maxImageSize = 1024 * 1024 * 3;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, content);
        imageService.setMaxSizeOfImage(maxImageSize);

        assertThrows(ImageSizeExceededException.class, () -> imageService.save(mockMultipartFile));
    }
}
