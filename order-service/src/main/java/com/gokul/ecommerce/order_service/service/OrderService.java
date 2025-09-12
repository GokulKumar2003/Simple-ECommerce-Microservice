package com.gokul.ecommerce.order_service.service;

import com.gokul.ecommerce.order_service.dto.OrderRequestDto;
import com.gokul.ecommerce.order_service.entity.OrderItem;
import com.gokul.ecommerce.order_service.entity.OrderStatus;
import com.gokul.ecommerce.order_service.entity.Orders;
import com.gokul.ecommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ModelMapper modelMapper,
                        RestClient restClient,
                        DiscoveryClient discoveryClient){
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
        this.discoveryClient = discoveryClient;
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

    //@Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod =
            "createOrderFallback")
   // @RateLimiter(name = "inventoryRateLimiter", fallbackMethod =
     //       "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto){

        ServiceInstance inventoryService = discoveryClient.getInstances(
                "inventory-service").getFirst();

        Double totalPrice =  restClient.put()
                .uri(inventoryService.getUri()+"/inventory/products/reduce" +
                        "-stock")
                .body(orderRequestDto)
                .retrieve()
                .body(Double.class);

        Orders orders = modelMapper.map(orderRequestDto, Orders.class);
        for(OrderItem item : orders.getItems()){
            item.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);
        Orders savedOrder = orderRepository.save(orders);

        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable){
        System.out.println("Inventory Fallback");
        return new OrderRequestDto();
    }
}
