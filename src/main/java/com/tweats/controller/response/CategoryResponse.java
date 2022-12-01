package com.tweats.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private long id;
    private String categoryName;
    private String imageLink;
    private boolean isOpen;
}
