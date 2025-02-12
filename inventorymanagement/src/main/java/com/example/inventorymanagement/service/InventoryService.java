package com.example.inventorymanagement.service;



import com.example.inventorymanagement.dto.InventoryList;
import com.example.inventorymanagement.dto.MerchantProductRequestDto;
import com.example.inventorymanagement.dto.OrderRequestDto;
import com.example.inventorymanagement.dto.OrderResponseDto;
import com.example.inventorymanagement.entity.MerchantInventory;
import com.example.inventorymanagement.entity.TotalInventory;
import com.example.inventorymanagement.enums.Operation;
import com.example.inventorymanagement.exception.InvalidRequestException;
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
        if (pincode == null || pincode.isEmpty() || productIds == null || productIds.isEmpty()) {
            throw new InvalidRequestException("Invalid pincode or product list cannot be empty.");
        }

        return totalInventoryRepository.findAll().stream()
                .filter(inv -> inv.getPincode().equals(pincode) && productIds.contains(inv.getProductId()))
                .collect(Collectors.toList());
    }



    public OrderResponseDto updateInventoryOnOrder(OrderRequestDto request) {
        if (request == null || request.getOrderList().isEmpty()) {
            throw new InvalidRequestException("Order request cannot be null or empty.");
        }
        List<Long> fulfilledMerchantIds = new ArrayList<>();

        for (InventoryList inventory : request.getOrderList()) {
            validateInventoryRequest(inventory);
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
        if (requestList == null || requestList.isEmpty()) {
            throw new InvalidRequestException("Product request list cannot be empty.");
        }
        List<Long> merchantIds = new ArrayList<>();

        for (MerchantProductRequestDto request : requestList) {
            // Check if the merchant already has this product in inventory
            validateMerchantProductRequest(request);
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

    private void validateInventoryRequest(InventoryList inventory) {
        if (inventory == null) {
            throw new InvalidRequestException("Inventory request cannot be null.");
        }
        if (inventory.getProductId() == null || inventory.getProductId() <= 0) {
            throw new InvalidRequestException("Invalid Product ID.");
        }
        if (inventory.getPincode() == null || inventory.getPincode().isEmpty()) {
            throw new InvalidRequestException("Pincode cannot be empty.");
        }
        if (inventory.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be greater than zero.");
        }
        if (inventory.getOperation() == Operation.PLUS && (inventory.getMerchantId() == null || inventory.getMerchantId() <= 0)) {
            throw new InvalidRequestException("Merchant ID is required for adding inventory.");
        }
    }

    private void validateMerchantProductRequest(MerchantProductRequestDto request) {
        if (request == null) {
            throw new InvalidRequestException("Product request cannot be null.");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new InvalidRequestException("Invalid Product ID.");
        }
        if (request.getPincode() == null || request.getPincode().isEmpty()) {
            throw new InvalidRequestException("Pincode cannot be empty.");
        }
        if (request.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be greater than zero.");
        }
        if (request.getMerchantId() == null || request.getMerchantId() <= 0) {
            throw new InvalidRequestException("Merchant ID is required.");
        }
    }






}
