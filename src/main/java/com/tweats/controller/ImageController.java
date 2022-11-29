package com.tweats.controller;

import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<byte[]> getImage(@PathVariable String id) throws ImageNotFoundException {
        return imageService.getImageResponse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestParam(value = "file") MultipartFile image) throws IOException, NotAnImageException, ImageSizeExceededException {
        imageService.save(image);
    }

}
