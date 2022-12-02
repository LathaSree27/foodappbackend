package com.tweats.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@Builder
public class ActiveOrdersResponse {
    private long count;
    private List<OrderResponse> orders;
}
