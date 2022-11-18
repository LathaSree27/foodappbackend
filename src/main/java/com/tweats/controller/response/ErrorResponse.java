package com.tweats.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.List;
@AllArgsConstructor
public class ErrorResponse {
    @JsonProperty
    private final String message;
    @JsonProperty
    private final List<String> details;

}
