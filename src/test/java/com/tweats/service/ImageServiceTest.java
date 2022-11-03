package com.tweats.service;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImageServiceTest {
    private ImageRepository imageRepository;

    @BeforeEach
    public void setup() {
        imageRepository = mock(ImageRepository.class);
    }

    @Test
    void shouldBeAbleToSaveImageFile() throws IOException, NotAnImageException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        ImageService imageService = new ImageService(imageRepository);

        imageService.save(mockMultipartFile);

        verify(imageRepository).save(any());

    }

    @Test
    void shouldThrowNotAnImageExceptionWhenTheFileIsNotPngOrJpegFormat() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.APPLICATION_FORM_URLENCODED_VALUE, "hello".getBytes());
        ImageService imageService = new ImageService(imageRepository);

        assertThrows(NotAnImageException.class, () -> imageService.save(mockMultipartFile));
    }

    @Test
    void shouldBeAbleToFetchImageWhenImageIdIsGiven() throws ImageNotFoundException {

        String imageId = "Image@123";
        Image image = new Image();
        when(imageRepository.findById(imageId))
                .thenReturn(Optional.of(image));
        ImageService imageService = new ImageService(imageRepository);

        Image imageFetched = imageService.getImage(imageId);

        assertThat(image, is(imageFetched));

    }

    @Test
    void shouldThrowImageNotFoundExceptionWhenInvalidImageIdIsGiven() {

        String imageId = "Image@123";
        ImageService imageService = new ImageService(imageRepository);

        assertThrows(ImageNotFoundException.class, () -> imageService.getImage(imageId));
    }

}
