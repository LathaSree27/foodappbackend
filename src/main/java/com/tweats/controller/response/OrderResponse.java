package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class OrderResponse {
    private long id;
    private Date date;
    private BigDecimal billAmount;
    private List<OrderedItemResponse> items;

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponse that = (OrderResponse) o;
        return id == that.id && date.equals(that.date) && billAmount.equals(that.billAmount) && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, billAmount, items);
    }
}
