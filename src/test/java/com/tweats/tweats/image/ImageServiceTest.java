package com.tweats.tweats.image;

import com.tweats.tweats.exceptions.NotAnImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ImageServiceTest {
    private ImageRepository imageRepository;


    @BeforeEach
    public void setup() {
        imageRepository = mock(ImageRepository.class);


    }

    @Test
    void shouldBeAbleToSaveImageWhenValidDetailsAreProvided() throws IOException, NotAnImageException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        ImageService imageService = new ImageService(imageRepository);
        imageService.save(mockMultipartFile);
        verify(imageRepository).save(any());

    }

    @Test
    void shouldNotBeAbleToSaveImageWhenGivenFileIsNotImage() throws IOException, NotAnImageException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.APPLICATION_FORM_URLENCODED_VALUE, "hello".getBytes());
        ImageService imageService = new ImageService(imageRepository);
        assertThrows(NotAnImageException.class, () -> imageService.save(mockMultipartFile));
    }
}
