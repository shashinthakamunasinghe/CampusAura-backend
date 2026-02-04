package com.example.campusaura.controller;

import com.example.campusaura.security.Roles;
import com.example.campusaura.service.UserService;
import com.example.campusaura.model.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Marketplace Controller for item selling/buying.
 * Enforces business rule: EXTERNAL_USER cannot sell items.
 */
@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final UserService userService;

    public MarketplaceController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sell an item on marketplace.
     * EXTERNAL_USER is explicitly blocked from selling.
     * Only STUDENT, COORDINATOR, and ADMIN can sell.
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'COORDINATOR', 'ADMIN')")
    @PostMapping("/sell")
    public ResponseEntity<Map<String, Object>> sellItem(
            @AuthenticationPrincipal String uid,
            @RequestBody Map<String, Object> itemData) {

        User user = userService.getUserByUid(uid);

        // Defense-in-depth: Double-check even if annotation handles it
        if (Roles.EXTERNAL_USER.equals(user.getRole())) {
            throw new AccessDeniedException("External users cannot sell items");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item listed for sale");
        response.put("sellerId", uid);
        response.put("sellerRole", user.getRole());
        response.put("itemData", itemData);

        return ResponseEntity.ok(response);
    }

    /**
     * Browse marketplace items.
     * All authenticated users (including EXTERNAL_USER) can browse.
     */
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> browseItems(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Marketplace items");
        response.put("userId", uid);
        response.put("userRole", user.getRole());
        response.put("canSell", !Roles.EXTERNAL_USER.equals(user.getRole()));

        return ResponseEntity.ok(response);
    }

    /**
     * Buy an item from marketplace.
     * All authenticated users (including EXTERNAL_USER) can buy.
     */
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyItem(
            @AuthenticationPrincipal String uid,
            @RequestBody Map<String, Object> purchaseData) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item purchased");
        response.put("buyerId", uid);
        response.put("buyerRole", user.getRole());
        response.put("purchaseData", purchaseData);

        return ResponseEntity.ok(response);
    }

    /**
     * Check if current user can sell items.
     * Utility endpoint for frontend.
     */
    @GetMapping("/can-sell")
    public ResponseEntity<Map<String, Object>> canSell(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);
        boolean canSell = !Roles.EXTERNAL_USER.equals(user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("canSell", canSell);
        response.put("role", user.getRole());
        response.put("reason", canSell ? "User has selling privileges" : "External users cannot sell items");

        return ResponseEntity.ok(response);
    }
}
