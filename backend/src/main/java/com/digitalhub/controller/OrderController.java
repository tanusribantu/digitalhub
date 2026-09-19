package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.OrderRequest;
import com.digitalhub.dto.OrderResponse;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody OrderRequest req
    ) {
        OrderResponse order = orderService.checkout(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.ok("Order placed successfully!", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("User orders", orderService.getUserOrders(principal.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Order details", orderService.getOrderById(id, principal.getId())));
    }
}