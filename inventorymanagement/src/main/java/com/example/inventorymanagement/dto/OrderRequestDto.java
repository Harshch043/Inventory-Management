package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderRequestDto {
    private Long productId;
    private String productName;
    private String pincode;
    private int quantity;
}