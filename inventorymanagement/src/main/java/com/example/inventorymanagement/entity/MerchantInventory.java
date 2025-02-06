package com.example.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merchant_inventory")
public class MerchantInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String merchantName;
    private String productName;
    private int quantity;
    private String pincode;

    public MerchantInventory(Long productId, String merchantName,String productName,int quantity, String pincode) {
        this.productId = productId;
        this.merchantName = merchantName;
        this.pincode = pincode;
        this.productName=productName;
        this.quantity = quantity;
    }

}