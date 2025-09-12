package com.gokul.ecommerce.order_service.controller;

import com.gokul.ecommerce.order_service.dto.OrderRequestDto;
import com.gokul.ecommerce.order_service.service.OrderService;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/hello")
    public String helloOrders(){
        return "Hello from Orders";
    }



    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(){
        List<OrderRequestDto> inventories = orderService.getAllOrders();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getInventoryById(@PathVariable Long id){
        OrderRequestDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        OrderRequestDto orderRequestDto1 =
                orderService.createOrder(orderRequestDto);

        return ResponseEntity.ok(orderRequestDto1);
    }
}
