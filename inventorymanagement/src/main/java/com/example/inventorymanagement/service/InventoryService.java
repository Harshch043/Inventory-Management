package com.example.inventorymanagement.service;



import com.example.inventorymanagement.dto.InventoryList;
import com.example.inventorymanagement.dto.MerchantProductRequestDto;
import com.example.inventorymanagement.dto.OrderRequestDto;
import com.example.inventorymanagement.dto.OrderResponseDto;
import com.example.inventorymanagement.entity.MerchantInventory;
import com.example.inventorymanagement.entity.TotalInventory;
import com.example.inventorymanagement.enums.Operation;
import com.example.inventorymanagement.repository.MerchantInventoryRepository;
import com.example.inventorymanagement.repository.TotalInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InventoryService {


    private final TotalInventoryRepository totalInventoryRepository;
    private final MerchantInventoryRepository merchantInventoryRepository;

    @Autowired
    public InventoryService(TotalInventoryRepository totalInventoryRepository,
                            MerchantInventoryRepository merchantInventoryRepository) {
        this.totalInventoryRepository = totalInventoryRepository;
        this.merchantInventoryRepository = merchantInventoryRepository;
    }

    public List<TotalInventory> getAvailableProducts(String pincode, List<Long> productIds) {
        return totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getPincode().equals(pincode) && productIds.contains(inv.getProductId()))
                .collect(Collectors.toList());
    }



    public OrderResponseDto updateInventoryOnOrder(OrderRequestDto request) {
        List<Long> fulfilledMerchantIds = new ArrayList<>();

        for (InventoryList inventory : request.getOrderList()) {
            MerchantInventory merchantInventory = merchantInventoryRepository.findAll().stream()
                    .filter(inv -> inv.getProductId().equals(inventory.getProductId()) &&
                            inv.getPincode().equals(inventory.getPincode()) &&
                            inv.getMerchantId()==(inventory.getMerchantId()))
                    .findFirst().orElse(null);

            if (merchantInventory != null) {
                if (inventory.getOperation() == Operation.MINUS && merchantInventory.getQuantity() >= inventory.getQuantity()) {
                    merchantInventory.setQuantity(merchantInventory.getQuantity() - inventory.getQuantity());
                    merchantInventoryRepository.save(merchantInventory);
                    fulfilledMerchantIds.add(merchantInventory.getMerchantId());
                } else if (inventory.getOperation() == Operation.PLUS) {
                    merchantInventory.setQuantity(merchantInventory.getQuantity() + inventory.getQuantity());
                    merchantInventoryRepository.save(merchantInventory);
                    fulfilledMerchantIds.add(merchantInventory.getMerchantId());
                }
            }
        }

        // Ensure the constructor is used correctly here
        return new OrderResponseDto(fulfilledMerchantIds);
    }

    public List<Long> addMerchantProducts(List<MerchantProductRequestDto> requestList) {
        List<Long> merchantIds = new ArrayList<>();

        for (MerchantProductRequestDto request : requestList) {
            // Save in Merchant Inventory
            MerchantInventory merchantInventory = new MerchantInventory(
                    (Long) null, request.getProductId(), request.getQuantity(), request.getPincode(), request.getMerchantId()
            );
            merchantInventoryRepository.save(merchantInventory);
            merchantIds.add(request.getMerchantId());

            // Update or create in Total Inventory
            TotalInventory totalInventory = totalInventoryRepository.findByProductIdAndPincode(
                    request.getProductId(), request.getPincode()
            ).orElse(null);

            if (totalInventory == null) {
                totalInventory = new TotalInventory(null, request.getProductId(), request.getQuantity(), request.getPincode());
            } else {
                totalInventory.setQuantity(totalInventory.getQuantity() + request.getQuantity());
            }
            totalInventoryRepository.save(totalInventory);
        }
        return merchantIds;
    }





}
