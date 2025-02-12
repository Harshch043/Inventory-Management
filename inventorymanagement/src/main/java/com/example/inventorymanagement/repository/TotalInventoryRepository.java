package com.example.inventorymanagement.repository;

import com.example.inventorymanagement.entity.TotalInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalInventoryRepository extends JpaRepository<TotalInventory, Long> {
    Optional<TotalInventory> findByProductIdAndPincode(Long productId, String pincode);
}
