package com.tweats.controller;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {
    @Mock
    ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @Test
    void shouldBeAbleToGetImageWhenIdIsGiven() throws ImageNotFoundException {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok().build();
        String imageId = "image@#$";
        when(imageService.getImageResponse(imageId)).thenReturn(responseEntity);

        imageController.getImage(imageId);

        verify(imageService).getImageResponse(imageId);
        assertThat(imageController.getImage(imageId), is(responseEntity));

    }

    @Test
    void shouldBeAbleToSaveImageWhenFileIsGiven() throws ImageSizeExceededException, IOException, NotAnImageException {
        MockMultipartFile imageFile = new MockMultipartFile("image.png", "Hello".getBytes());

        imageController.save(imageFile);

        verify(imageService).save(imageFile);
    }
}
