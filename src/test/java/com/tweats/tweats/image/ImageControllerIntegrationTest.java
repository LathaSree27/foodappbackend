package com.tweats.tweats.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweats.tweats.TweatsApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser
public class ImageControllerIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MockMvc mockMvc;

   @BeforeEach
   public  void beforeEach(){
       imageRepository.deleteAll();
   }

   @AfterEach
   public  void afterEach(){
       imageRepository.deleteAll();
   }

    @Test
    void shouldBeAbleToFetchImageWhenIdIsProvided() throws Exception {
        Image image = new Image( "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(),22L);
        Image imageFetched = imageRepository.save(image);
        String imageId = imageFetched.getId();
        mockMvc.perform(get("/images/"+imageId))
                        .andExpect(status().isOk());
    }
}
