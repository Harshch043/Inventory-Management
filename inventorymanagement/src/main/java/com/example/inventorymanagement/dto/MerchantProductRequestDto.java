package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantProductRequestDto {
    private Long productId;
    private String merchantName;
    private String productName;
    private int quantity;
    private String pincode;

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getPincode() {
        return pincode;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMerchantName() {
        return merchantName;
    }
}
