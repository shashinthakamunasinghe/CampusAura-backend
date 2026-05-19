package com.example.campusaura.service;

import com.example.campusaura.dto.ProductResponseDTO;
import com.example.campusaura.model.Product;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "products";

    // Get all products
    public List<ProductResponseDTO> getAllProducts() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<ProductResponseDTO> result = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            try {
                Product product = documentToProduct(doc);
                result.add(productToDTO(product));
            } catch (Exception e) {
                logger.error("Skipping malformed product document '{}': {}", doc.getId(), e.getMessage());
            }
        }
        return result;
    }

    // Get product by ID
    public ProductResponseDTO getProductById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME).document(id).get().get();
        
        if (!document.exists()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        Product product = documentToProduct(document);
        return productToDTO(product);
    }

    // Get products by status
    public List<ProductResponseDTO> getProductsByStatus(Product.ProductStatus status) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("status", status.toString());
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToProduct)
                .map(this::productToDTO)
                .collect(Collectors.toList());
    }

    // Get count of sold products
    public long getSoldProductsCount() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", Product.ProductStatus.SOLD.toString());
        ApiFuture<QuerySnapshot> future = query.get();
        return future.get().getDocuments().size();
    }

    // Delete product (soft delete by updating status)
    public void deleteProduct(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        docRef.delete().get();
    }

    // Soft delete product (marks as deleted without removing)
    public void softDeleteProduct(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", Product.ProductStatus.DELETED.toString());
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();
    }

    // Helper methods
    private Product documentToProduct(DocumentSnapshot document) {
        Product product = new Product();
        product.setId(document.getId());
        product.setName(document.getString("name"));
        product.setDescription(document.getString("description"));

        Double price = document.getDouble("price");
        if (price != null) {
            product.setPrice(price);
        }

        product.setCategory(document.getString("category"));
        product.setImageUrl(document.getString("imageUrl"));
        product.setSellerId(document.getString("sellerId"));
        product.setSellerName(document.getString("sellerName"));

        // Parse status — handle unknown values gracefully
        String statusStr = document.getString("status");
        if (statusStr != null) {
            try {
                product.setStatus(Product.ProductStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown product status '{}' for doc {}, defaulting to PENDING", statusStr, document.getId());
                product.setStatus(Product.ProductStatus.PENDING);
            }
        }

        // Parse createdAt — may be stored as Firestore Timestamp OR ISO string
        product.setCreatedAt(parseDateTime(document, "createdAt"));
        product.setUpdatedAt(parseDateTime(document, "updatedAt"));
        product.setSoldAt(parseDateTime(document, "soldAt"));

        return product;
    }

    /**
     * Safely parse a date/time field that may be stored as a Firestore Timestamp
     * OR as an ISO-8601 string. Returns null if the field is absent or unparseable.
     */
    private LocalDateTime parseDateTime(DocumentSnapshot document, String field) {
        // Try Firestore Timestamp first (most common when saved via Firebase SDK)
        try {
            Timestamp ts = document.getTimestamp(field);
            if (ts != null) {
                return ts.toDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (Exception ignored) {
            // Field exists but is not a Timestamp — fall through to String parsing
        }

        // Fallback: try as ISO-8601 string
        try {
            String str = document.getString(field);
            if (str != null && !str.isEmpty()) {
                return LocalDateTime.parse(str);
            }
        } catch (Exception e) {
            logger.warn("Could not parse date field '{}' for doc {}: {}", field, document.getId(), e.getMessage());
        }

        return null;
    }

    private ProductResponseDTO productToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setSellerId(product.getSellerId());
        dto.setSellerName(product.getSellerName());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setSoldAt(product.getSoldAt());
        return dto;
    }

    /**
     * Update product status (for admin approval/rejection)
     */
    public ProductResponseDTO updateProductStatus(String id, Product.ProductStatus status) 
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status.toString());
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();

        // Return updated product
        Product product = documentToProduct(docRef.get().get());
        return productToDTO(product);
    }

    /**
     * Get count of pending products (for admin dashboard)
     */
    public long getPendingProductsCount() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", Product.ProductStatus.PENDING.toString());
        ApiFuture<QuerySnapshot> future = query.get();
        return future.get().getDocuments().size();
    }
}
