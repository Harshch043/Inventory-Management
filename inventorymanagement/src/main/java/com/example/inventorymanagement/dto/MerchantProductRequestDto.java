package com.example.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantProductRequestDto {
    private Long productId;
    private int quantity;
    private String pincode;
    private Long merchantId;

    public Long getMerchantId(){
        return merchantId;
    }
    public Long getProductId() {
        return productId;
    }


    public String getPincode() {
        return pincode;
    }

    public int getQuantity() {
        return quantity;
    }

}
