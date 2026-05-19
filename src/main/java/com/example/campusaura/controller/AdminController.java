package com.example.campusaura.controller;

import com.example.campusaura.dto.*;
import com.example.campusaura.model.Product;
import com.example.campusaura.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CoordinatorService coordinatorService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SalesService salesService;

    // ==================== DASHBOARD SECTION ====================

    /**
     * Get dashboard statistics
     * GET /api/admin/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== COORDINATOR MANAGEMENT SECTION ====================

    /**
     * Register a new coordinator
     * POST /api/admin/coordinators
     */
    @PostMapping("/coordinators")
    public ResponseEntity<CoordinatorResponseDTO> registerCoordinator(@RequestBody CoordinatorRequestDTO request) {
        try {
            CoordinatorResponseDTO coordinator = coordinatorService.registerCoordinator(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(coordinator);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all coordinators
     * GET /api/admin/coordinators
     */
    @GetMapping("/coordinators")
    public ResponseEntity<List<CoordinatorResponseDTO>> getAllCoordinators() {
        try {
            List<CoordinatorResponseDTO> coordinators = coordinatorService.getAllCoordinators();
            return ResponseEntity.ok(coordinators);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get coordinator by ID
     * GET /api/admin/coordinators/{id}
     */
    @GetMapping("/coordinators/{id}")
    public ResponseEntity<CoordinatorResponseDTO> getCoordinatorById(@PathVariable String id) {
        try {
            CoordinatorResponseDTO coordinator = coordinatorService.getCoordinatorById(id);
            return ResponseEntity.ok(coordinator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update coordinator
     * PUT /api/admin/coordinators/{id}
     */
    @PutMapping("/coordinators/{id}")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinator(
            @PathVariable String id, 
            @RequestBody CoordinatorRequestDTO request) {
        try {
            CoordinatorResponseDTO coordinator = coordinatorService.updateCoordinator(id, request);
            return ResponseEntity.ok(coordinator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update coordinator status (active/inactive)
     * PATCH /api/admin/coordinators/{id}/status
     */
    @PatchMapping("/coordinators/{id}/status")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinatorStatus(
            @PathVariable String id, 
            @RequestBody Map<String, Boolean> statusUpdate) {
        try {
            boolean active = statusUpdate.get("active");
            CoordinatorResponseDTO coordinator = coordinatorService.updateCoordinatorStatus(id, active);
            return ResponseEntity.ok(coordinator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete coordinator
     * DELETE /api/admin/coordinators/{id}
     */
    @DeleteMapping("/coordinators/{id}")
    public ResponseEntity<Map<String, String>> deleteCoordinator(@PathVariable String id) {
        try {
            coordinatorService.deleteCoordinator(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coordinator deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get degree programmes for dropdown
     * GET /api/admin/coordinators/degree-programmes
     */
    @GetMapping("/coordinators/degree-programmes")
    public ResponseEntity<List<String>> getDegreeProgrammes() {
        List<String> programmes = List.of(
            "Animal Production and Food Technology",
            "Export Agriculture",
            "Aquatic Resources Technology",
            "Tea Technology and Value Addition",
            "Computer Science and Technology",
            "Industrial Information Technology",
            "Science & Technology",
            "Mineral Resources and Technology",
            "Entrepreneurship & Management Studies",
            "Hospitality, Tourism & Events Management",
            "Human Resource Development",
            "English Language & Applied Linguistics",
            "Engineering Technology",
            "Biosystems Technology Honours",
            "Information and Communication Technology Honours"
        );
        return ResponseEntity.ok(programmes);
    }

    /**
     * Get departments for dropdown (same as degree programmes for now)
     * GET /api/admin/coordinators/departments
     */
    @GetMapping("/coordinators/departments")
    public ResponseEntity<List<String>> getDepartments() {
        List<String> departments = List.of(
            "Animal Production and Food Technology",
            "Export Agriculture",
            "Aquatic Resources Technology",
            "Tea Technology and Value Addition",
            "Computer Science and Technology",
            "Industrial Information Technology",
            "Science & Technology",
            "Mineral Resources and Technology",
            "Entrepreneurship & Management Studies",
            "Hospitality, Tourism & Events Management",
            "Human Resource Development",
            "English Language & Applied Linguistics",
            "Engineering Technology",
            "Biosystems Technology Honours",
            "Information and Communication Technology Honours"
        );
        return ResponseEntity.ok(departments);
    }

    // ==================== EVENT MANAGEMENT SECTION ====================

    /**
     * Get all events with coordinator names for admin management
     * GET /api/admin/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<AdminEventDTO>> getAllEvents() {
        try {
            List<AdminEventDTO> events = eventService.getAllEventsForAdmin();
            return ResponseEntity.ok(events);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event by ID
     * GET /api/admin/events/{id}
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable String id) {
        try {
            EventResponseDTO event = eventService.getEventByIdDTO(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete event
     * DELETE /api/admin/events/{id}
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Filter events by category
     * GET /api/admin/events/filter?category={category}
     */
    @GetMapping("/events/filter")
    public ResponseEntity<List<EventResponseDTO>> filterEventsByCategory(@RequestParam String category) {
        try {
            List<EventResponseDTO> events = eventService.getEventsByCategory(category);
            return ResponseEntity.ok(events);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Approve event
     * POST /api/admin/events/{id}/approve
     */
    @PostMapping("/events/{id}/approve")
    public ResponseEntity<EventResponseDTO> approveEvent(@PathVariable String id) {
        try {
            EventResponseDTO event = eventService.updateEventStatus(id, "PUBLISHED");
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reject event
     * POST /api/admin/events/{id}/reject
     */
    @PostMapping("/events/{id}/reject")
    public ResponseEntity<EventResponseDTO> rejectEvent(@PathVariable String id) {
        try {
            EventResponseDTO event = eventService.updateEventStatus(id, "REJECTED");
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get pending events count
     * GET /api/admin/events/pending/count
     */
    @GetMapping("/events/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingEventsCount() {
        try {
            long count = eventService.getPendingEventsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== USER MANAGEMENT SECTION ====================

    /**
     * Get all users
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userManagementService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get university students
     * GET /api/admin/users/university-students
     */
    @GetMapping("/users/university-students")
    public ResponseEntity<List<UserResponseDTO>> getUniversityStudents() {
        try {
            List<UserResponseDTO> users = userManagementService.getUsersByRole("STUDENT");
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get external users
     * GET /api/admin/users/external-users
     */
    @GetMapping("/users/external-users")
    public ResponseEntity<List<UserResponseDTO>> getExternalUsers() {
        try {
            List<UserResponseDTO> users = userManagementService.getUsersByRole("EXTERNAL_USER");
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get pending verification users
     * GET /api/admin/users/pending-verification
     */
    @GetMapping("/users/pending-verification")
    public ResponseEntity<List<UserResponseDTO>> getPendingVerificationUsers() {
        try {
            List<UserResponseDTO> users = userManagementService.getPendingVerificationUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user statistics
     * GET /api/admin/users/stats
     */
    @GetMapping("/users/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        try {
            UserStatsDTO stats = userManagementService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update user active status — NOTE: The User entity does not have an 'active' field.
     * This endpoint is kept for API compatibility; it is a no-op that returns the current user.
     * PATCH /api/admin/users/{id}/status
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Map<String, String>> updateUserStatus(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> statusUpdate) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User status update not supported in current user model");
        return ResponseEntity.ok(response);
    }

    /**
     * Verify student ID (approve = true / reject = false)
     * PATCH /api/admin/users/{id}/verify
     * Body: { "status": "VERIFIED" } or { "status": "REJECTED" }
     */
    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<UserResponseDTO> verifyStudent(
            @PathVariable String id,
            @RequestBody Map<String, String> verificationUpdate) {
        try {
            String statusStr = verificationUpdate.get("status");
            // Map string status to boolean: "VERIFIED" → true, anything else → false
            boolean verified = "VERIFIED".equalsIgnoreCase(statusStr);
            UserResponseDTO user = userManagementService.verifyStudent(id, verified);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete user
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        try {
            userManagementService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== PRODUCT MANAGEMENT SECTION ====================

    /**
     * Get all products
     * GET /api/admin/products
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        try {
            List<ProductResponseDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product by ID
     * GET /api/admin/products/{id}
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        try {
            ProductResponseDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete product
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Approve product
     * POST /api/admin/products/{id}/approve
     */
    @PostMapping("/products/{id}/approve")
    public ResponseEntity<ProductResponseDTO> approveProduct(@PathVariable String id) {
        try {
            ProductResponseDTO product = productService.updateProductStatus(id, Product.ProductStatus.APPROVED);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Disable product
     * POST /api/admin/products/{id}/disable
     */
    @PostMapping("/products/{id}/disable")
    public ResponseEntity<ProductResponseDTO> disableProduct(@PathVariable String id) {
        try {
            ProductResponseDTO product = productService.updateProductStatus(id, Product.ProductStatus.DELETED);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get pending products count
     * GET /api/admin/products/pending/count
     */
    @GetMapping("/products/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingProductsCount() {
        try {
            long count = productService.getPendingProductsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== PAYMENT SECTION ====================

    /**
     * Get payment statistics
     * GET /api/admin/payments/stats
     */
    @GetMapping("/payments/stats")
    public ResponseEntity<PaymentStatsDTO> getPaymentStats() {
        try {
            PaymentStatsDTO stats = transactionService.getPaymentStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all transactions
     * GET /api/admin/payments/transactions
     */
    @GetMapping("/payments/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recent transactions
     * GET /api/admin/payments/transactions/recent?limit={limit}
     */
    @GetMapping("/payments/transactions/recent")
    public ResponseEntity<List<TransactionResponseDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getRecentTransactions(limit);
            return ResponseEntity.ok(transactions);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== SALES TRACKING SECTION ====================

    /**
     * Get all ticket sales
     * GET /api/admin/sales/tickets
     */
    @GetMapping("/sales/tickets")
    public ResponseEntity<List<TicketSaleDTO>> getAllTicketSales() {
        try {
            List<TicketSaleDTO> sales = salesService.getAllTicketSales();
            return ResponseEntity.ok(sales);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all product sales
     * GET /api/admin/sales/products
     */
    @GetMapping("/sales/products")
    public ResponseEntity<List<ProductSaleDTO>> getAllProductSales() {
        try {
            List<ProductSaleDTO> sales = salesService.getAllProductSales();
            return ResponseEntity.ok(sales);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
