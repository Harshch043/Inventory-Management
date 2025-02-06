package com.example.inventorymanagement.repository;

import com.example.inventorymanagement.entity.TotalInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TotalInventoryRepository extends JpaRepository<TotalInventory, Long> {
}
