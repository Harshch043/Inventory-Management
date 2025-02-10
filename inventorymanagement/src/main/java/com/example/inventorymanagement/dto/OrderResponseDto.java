package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private List<Long> merchantId;

    public OrderResponseDto(List<Long> merchantId) {
        this.merchantId = merchantId;
    }

    // Getter and Setter
    public List<Long> getMerchantIds() {
        return merchantId;
    }

    public void setMerchantIds(List<Long> merchantIds) {
        this.merchantId = merchantIds;
    }

}
