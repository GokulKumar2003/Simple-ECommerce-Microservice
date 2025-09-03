package com.gokul.ecommerce.order_service.service;

import com.gokul.ecommerce.order_service.dto.OrderRequestDto;
import com.gokul.ecommerce.order_service.entity.Orders;
import com.gokul.ecommerce.order_service.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ModelMapper modelMapper){
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    public List<OrderRequestDto> getAllOrders(){
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(order -> modelMapper.map(order,
                OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById(Long id){
        Orders order =
                orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Order with id: {} not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }
}
