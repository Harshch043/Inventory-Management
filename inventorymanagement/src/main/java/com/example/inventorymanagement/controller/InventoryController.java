package com.example.inventorymanagement.controller;


import com.example.inventorymanagement.dto.MerchantProductRequestDto;
import com.example.inventorymanagement.dto.OrderRequestDto;
import com.example.inventorymanagement.dto.OrderResponseDto;
import com.example.inventorymanagement.entity.TotalInventory;
import com.example.inventorymanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }



    @GetMapping("/available")
    public List<TotalInventory> getAvailableProducts(@RequestParam String pincode, @RequestParam List<Long> productIds) {
        return inventoryService.getAvailableProducts(pincode, productIds);
    }


    @PostMapping("/update")
    public OrderResponseDto updateInventory(@RequestBody OrderRequestDto request) {
        return inventoryService.updateInventoryOnOrder(request);
    }

    @PostMapping("/merchant/add")
    public ResponseEntity<OrderResponseDto> addMerchantProduct(@RequestBody List<MerchantProductRequestDto> requestList) {
        List<Long> merchantIds = inventoryService.addMerchantProducts(requestList);
        return ResponseEntity.ok(new OrderResponseDto(merchantIds));
    }


}
