package com.example.inventorymanagement.controller;


import com.example.inventorymanagement.dto.MerchantProductRequestDto;
import com.example.inventorymanagement.dto.OrderRequestDto;
import com.example.inventorymanagement.dto.OrderResponseDto;
import com.example.inventorymanagement.entity.TotalInventory;
import com.example.inventorymanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    @GetMapping("/available")
    public List<TotalInventory> getAvailableProducts(@RequestParam String pincode) {
        return inventoryService.getAvailableProducts(pincode);
    }

    @PostMapping("/order")
    public OrderResponseDto placeOrder(@RequestBody OrderRequestDto request) {
        inventoryService.updateInventoryOnOrder(request);
        return new OrderResponseDto("Order placed successfully");
    }

    @PostMapping("/cancel")
    public OrderResponseDto cancelOrder(@RequestBody OrderRequestDto request) {
        inventoryService.cancelOrder(request);
        return new OrderResponseDto("Order cancelled successfully");
    }

    @PostMapping("/merchant/add")
    public OrderResponseDto addMerchantProduct(@RequestBody MerchantProductRequestDto request) {
        inventoryService.addMerchantProduct(request);
        return new OrderResponseDto("Product added successfully by merchant");
    }

}
