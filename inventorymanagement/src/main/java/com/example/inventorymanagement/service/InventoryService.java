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
            if (inventory.getOperation() == Operation.PLUS) {
                // Merchant ID is provided -> Directly add inventory
                MerchantInventory merchantInventory = merchantInventoryRepository.findByProductIdAndPincodeAndMerchantId(
                        inventory.getProductId(), inventory.getPincode(), inventory.getMerchantId()
                ).orElse(null);

                if (merchantInventory == null) {
                    // Create new inventory if merchant does not have this product
                    merchantInventory = new MerchantInventory(
                            null, inventory.getProductId(), inventory.getQuantity(), inventory.getPincode(), inventory.getMerchantId()
                    );
                } else {
                    // Update existing inventory
                    merchantInventory.setQuantity(merchantInventory.getQuantity() + inventory.getQuantity());
                }

                merchantInventoryRepository.save(merchantInventory);
                fulfilledMerchantIds.add(inventory.getMerchantId());

            } else if (inventory.getOperation() == Operation.MINUS) {
                // Merchant ID is NOT provided -> Find merchants who have stock
                List<MerchantInventory> availableMerchants = merchantInventoryRepository.findByProductIdAndPincode(
                        inventory.getProductId(), inventory.getPincode()
                );

                int remainingQuantity = inventory.getQuantity();

                for (MerchantInventory merchantInventory : availableMerchants) {
                    if (merchantInventory.getQuantity() >= remainingQuantity) {
                        // This merchant can fully fulfill the order
                        merchantInventory.setQuantity(merchantInventory.getQuantity() - remainingQuantity);
                        merchantInventoryRepository.save(merchantInventory);
                        fulfilledMerchantIds.add(merchantInventory.getMerchantId());
                        break;  // Order fulfilled
                    } else {
                        // Partially fulfill from this merchant and continue to the next one
                        remainingQuantity -= merchantInventory.getQuantity();
                        merchantInventory.setQuantity(0);
                        merchantInventoryRepository.save(merchantInventory);
                        fulfilledMerchantIds.add(merchantInventory.getMerchantId());
                    }
                }
            }
        }

        return new OrderResponseDto(fulfilledMerchantIds);
    }


    public List<Long> addMerchantProducts(List<MerchantProductRequestDto> requestList) {
        List<Long> merchantIds = new ArrayList<>();

        for (MerchantProductRequestDto request : requestList) {
            // Check if the merchant already has this product in inventory
            MerchantInventory merchantInventory = merchantInventoryRepository
                    .findByProductIdAndPincodeAndMerchantId(request.getProductId(), request.getPincode(), request.getMerchantId())
                    .orElse(null);

            if (merchantInventory == null) {
                // If merchant does not have this product, create a new entry
                merchantInventory = new MerchantInventory(null, request.getProductId(), request.getQuantity(), request.getPincode(), request.getMerchantId());
            } else {
                // If merchant already has this product, update the quantity
                merchantInventory.setQuantity(merchantInventory.getQuantity() + request.getQuantity());
            }
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
