package com.tweats.view;

import com.tweats.TweatsApplication;
import com.tweats.model.Image;
import com.tweats.repo.ImageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
public class ImageControllerIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        imageRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        imageRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToFetchImageWhenIdIsProvided() throws Exception {
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 22L);
        Image imageFetched = imageRepository.save(image);
        String imageId = imageFetched.getId();

        mockMvc.perform(get("/images/" + imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes("hello".getBytes()))
                .andReturn();
    }

    @Test
    void shouldThrowImageNotFoundErrorWhenImageIsNotPresent() throws Exception {
        String imageId = "Image@3456";

        mockMvc.perform(get("/images/" + imageId))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldBeAbleToSaveImageWhenJpegOrPngFileIsProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, "hello".getBytes());

        mockMvc.perform(multipart("/images").file(mockMultipartFile))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldThrowNotAnImageErrorWhenGivenFileIsNotJpegOrPngFormat() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.APPLICATION_ATOM_XML_VALUE, "hello".getBytes());

        mockMvc.perform(multipart("/images").file(mockMultipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowImageSizeExceededErrorWhenImageSizeIsGreaterThanConfiguredSize() throws Exception {
        byte[] content = new byte[1024 * 1024 * 4];
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, content);

        mockMvc.perform(multipart("/images").file(mockMultipartFile))
                .andExpect(status().isBadRequest());
    }

}
