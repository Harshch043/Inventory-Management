package com.example.inventorymanagement.service;


import com.example.inventorymanagement.dto.MerchantProductRequestDto;
import com.example.inventorymanagement.dto.OrderRequestDto;
import com.example.inventorymanagement.entity.MerchantInventory;
import com.example.inventorymanagement.entity.TotalInventory;
import com.example.inventorymanagement.repository.MerchantInventoryRepository;
import com.example.inventorymanagement.repository.TotalInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private final TotalInventoryRepository totalInventoryRepository;


    @Autowired
    private final MerchantInventoryRepository merchantInventoryRepository;


    public List<TotalInventory> getAvailableProducts(String pincode) {
        return totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getPincode().equals(pincode))
                .toList();
    }

    public void updateInventoryOnOrder(OrderRequestDto request) {
        TotalInventory totalInventory = totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getProductId().equals(request.getProductId()) && inv.getPincode().equals(request.getPincode()))
                .findFirst().orElse(null);
        if (totalInventory != null && totalInventory.getQuantity() >= request.getQuantity()) {
            totalInventory.setQuantity(totalInventory.getQuantity() - request.getQuantity());
            totalInventoryRepository.save(totalInventory);
        }
    }

    public void cancelOrder(OrderRequestDto request) {
        TotalInventory totalInventory = totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getProductId().equals(request.getProductId()) && inv.getPincode().equals(request.getPincode()))
                .findFirst().orElse(null);
        if (totalInventory != null) {
            totalInventory.setQuantity(totalInventory.getQuantity() + request.getQuantity());
            totalInventoryRepository.save(totalInventory);
        }
    }

    public void addMerchantProduct(MerchantProductRequestDto request) {
        MerchantInventory merchantInventory = new MerchantInventory(null, request.getProductId(), request.getMerchantName(), request.getProductName(), request.getQuantity(), request.getPincode());
        merchantInventoryRepository.save(merchantInventory);

        TotalInventory totalInventory = totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getProductId().equals(request.getProductId()) && inv.getPincode().equals(request.getPincode()))
                .findFirst().orElse(null);
        if (totalInventory == null) {
            totalInventory = new TotalInventory(null, request.getProductId(), request.getProductName(), request.getQuantity(), request.getPincode());
        } else {
            totalInventory.setQuantity(totalInventory.getQuantity() + request.getQuantity());
        }
        totalInventoryRepository.save(totalInventory);
    }

}
