package com.tweats.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Builder
public class OrdersResponse {
    private long count;
    private BigDecimal revenue;
    private List<OrderResponse> orders;
}
