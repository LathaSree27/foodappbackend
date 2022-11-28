package com.tweats.controller.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VendorCategoryResponse {

    private long id;

    public String getId() {
        return String.valueOf(id);
    }
}
