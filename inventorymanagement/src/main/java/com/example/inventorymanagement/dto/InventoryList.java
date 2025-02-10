package com.example.inventorymanagement.dto;

import com.example.inventorymanagement.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryList{
    private Long productId;
    private String pincode;
    private int quantity;
    private Long merchantId;
    private Operation operation;

    public InventoryList(Long productId, String pincode, int quantity, Long merchantId, Operation operation) {
        this.productId = productId;
        this.pincode = pincode;
        this.quantity = quantity;
        this.merchantId = merchantId;
        this.operation = operation;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

}
