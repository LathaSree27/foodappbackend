package com.tweats.controller;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("images")
@AllArgsConstructor
public class ImageController {

    private ImageService imageService;

    @GetMapping("{id}")
    public ResponseEntity getImage(@PathVariable String id) throws ImageNotFoundException {
        Image image = imageService.getImage(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"")
                .contentType(MediaType.valueOf(image.getContentType()))
                .body(image.getData());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestParam(value = "file" ) MultipartFile image) throws IOException, NotAnImageException, ImageSizeExceededException {
        imageService.save(image);
    }

}
