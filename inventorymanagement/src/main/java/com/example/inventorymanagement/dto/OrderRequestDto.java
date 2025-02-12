package com.example.inventorymanagement.dto;

import com.example.inventorymanagement.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private List<InventoryList> orderList;

    public OrderRequestDto(List<InventoryList> orderList) {
        this.orderList = orderList;
    }

    // Getter and Setter
    public List<InventoryList> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<InventoryList> orderList) {
        this.orderList = orderList;
    }

}




/* input get from user to update inventory is in format like this
{
   {
      productId: 5,
      quantity: 10;
      operation: PLUS;
   }
   {
      productId: 3,
      quantity: 8;
      operation: PLUS;
   }
}
 */