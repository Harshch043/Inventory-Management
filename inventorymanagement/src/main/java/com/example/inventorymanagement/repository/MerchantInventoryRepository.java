package com.example.inventorymanagement.repository;

import com.example.inventorymanagement.entity.MerchantInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantInventoryRepository extends JpaRepository<MerchantInventory, Long> {
    Optional<MerchantInventory> findByProductIdAndPincodeAndMerchantId(Long productId, String pincode, Long merchantId);

    List<MerchantInventory> findByProductIdAndPincode(Long productId, String pincode);


}
