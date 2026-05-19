package com.example.campusaura.controller;

import com.example.campusaura.dto.ProductSaleDTO;
import com.example.campusaura.dto.TicketSaleDTO;
import com.example.campusaura.model.User;
import com.example.campusaura.service.SalesService;
import com.example.campusaura.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Autowired
    private SalesService salesService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Create a Stripe PaymentIntent for ticket purchase
     * POST /api/payments/create-ticket-intent
     */
    @PostMapping("/create-ticket-intent")
    public ResponseEntity<?> createTicketPaymentIntent(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            extractUserIdFromToken(authHeader); // Verify auth

            double amountLKR = ((Number) body.get("amount")).doubleValue();
            // Convert LKR to USD (approximate rate: 1 USD = 320 LKR)
            long amountUSD = Math.round((amountLKR / 320.0) * 100); // Stripe expects cents
            if (amountUSD < 50) amountUSD = 50; // Stripe minimum is $0.50

            String eventTitle = (String) body.get("eventTitle");

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountUSD)
                    .setCurrency("usd")
                    .setDescription("Ticket purchase for: " + eventTitle)
                    .putMetadata("type", "ticket")
                    .putMetadata("eventId", (String) body.get("eventId"))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            response.put("paymentIntentId", intent.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create payment intent: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Confirm ticket purchase after successful payment
     * POST /api/payments/confirm-ticket
     */
    @PostMapping("/confirm-ticket")
    public ResponseEntity<?> confirmTicketPurchase(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TicketSaleDTO saleData) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            User user = userService.getUserByUid(uid);

            saleData.setUserId(uid);
            saleData.setUserName(user.getName() != null ? user.getName() : user.getEmail().split("@")[0]);
            saleData.setUserEmail(user.getEmail());

            TicketSaleDTO saved = salesService.saveTicketSale(saleData);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to confirm ticket purchase: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Create a Stripe PaymentIntent for product purchase
     * POST /api/payments/create-product-intent
     */
    @PostMapping("/create-product-intent")
    public ResponseEntity<?> createProductPaymentIntent(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            extractUserIdFromToken(authHeader);

            double amountLKR = ((Number) body.get("amount")).doubleValue();
            long amountUSD = Math.round((amountLKR / 320.0) * 100);
            if (amountUSD < 50) amountUSD = 50;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountUSD)
                    .setCurrency("usd")
                    .setDescription("Product purchase from CampusAura Marketplace")
                    .putMetadata("type", "product")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            response.put("paymentIntentId", intent.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create payment intent: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Confirm product purchase after successful payment
     * POST /api/payments/confirm-product
     */
    @PostMapping("/confirm-product")
    public ResponseEntity<?> confirmProductPurchase(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProductSaleDTO saleData) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            User user = userService.getUserByUid(uid);

            saleData.setUserId(uid);
            saleData.setUserName(user.getName() != null ? user.getName() : user.getEmail().split("@")[0]);
            saleData.setUserEmail(user.getEmail());

            ProductSaleDTO saved = salesService.saveProductSale(saleData);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to confirm product purchase: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private String extractUserIdFromToken(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return decodedToken.getUid();
    }
}
