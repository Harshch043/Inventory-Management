package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MerchantProductRequestDto {
    private Long productId;
    private String merchantName;
    private String productName;
    private int quantity;
    private String pincode;
}
