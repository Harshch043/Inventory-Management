package com.example.inventorymanagement.repository;

import com.example.inventorymanagement.entity.MerchantInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantInventoryRepository extends JpaRepository<MerchantInventory, Long> {
}
