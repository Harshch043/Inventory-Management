package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderResponseDto {
    private String message;

    public OrderResponseDto(String message) {
        this.message = message;
    }
}
