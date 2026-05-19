package com.example.campusaura.service;

import com.example.campusaura.dto.ProductSaleDTO;
import com.example.campusaura.dto.TicketSaleDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SalesService {

    @Autowired
    private Firestore firestore;

    private static final String TICKET_SALES_COLLECTION = "ticket_sales";
    private static final String PRODUCT_SALES_COLLECTION = "product_sales";

    // ==================== TICKET SALES ====================

    public TicketSaleDTO saveTicketSale(TicketSaleDTO sale) throws ExecutionException, InterruptedException {
        String saleId = UUID.randomUUID().toString();
        sale.setSaleId(saleId);
        if (sale.getPurchasedAt() == null) {
            sale.setPurchasedAt(Instant.now().toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("saleId", sale.getSaleId());
        data.put("eventId", sale.getEventId());
        data.put("eventTitle", sale.getEventTitle());
        data.put("userId", sale.getUserId());
        data.put("userName", sale.getUserName());
        data.put("userEmail", sale.getUserEmail());
        data.put("ticketCategory", sale.getTicketCategory());
        data.put("ticketCount", sale.getTicketCount());
        data.put("pricePerTicket", sale.getPricePerTicket());
        data.put("totalAmount", sale.getTotalAmount());
        data.put("stripePaymentId", sale.getStripePaymentId());
        data.put("purchasedAt", sale.getPurchasedAt());

        firestore.collection(TICKET_SALES_COLLECTION).document(saleId).set(data).get();
        return sale;
    }

    public List<TicketSaleDTO> getAllTicketSales() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(TICKET_SALES_COLLECTION)
                .get().get().getDocuments();

        return docs.stream().map(this::docToTicketSale)
                .sorted((a, b) -> {
                    String dateA = a.getPurchasedAt() != null ? a.getPurchasedAt() : "";
                    String dateB = b.getPurchasedAt() != null ? b.getPurchasedAt() : "";
                    return dateB.compareTo(dateA); // DESC
                })
                .collect(Collectors.toList());
    }

    private TicketSaleDTO docToTicketSale(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        TicketSaleDTO dto = new TicketSaleDTO();
        dto.setSaleId((String) data.get("saleId"));
        dto.setEventId((String) data.get("eventId"));
        dto.setEventTitle((String) data.get("eventTitle"));
        dto.setUserId((String) data.get("userId"));
        dto.setUserName((String) data.get("userName"));
        dto.setUserEmail((String) data.get("userEmail"));
        dto.setTicketCategory((String) data.get("ticketCategory"));
        Object ticketCount = data.get("ticketCount");
        dto.setTicketCount(ticketCount instanceof Number ? ((Number) ticketCount).intValue() : 0);
        Object pricePerTicket = data.get("pricePerTicket");
        dto.setPricePerTicket(pricePerTicket instanceof Number ? ((Number) pricePerTicket).doubleValue() : 0);
        Object totalAmount = data.get("totalAmount");
        dto.setTotalAmount(totalAmount instanceof Number ? ((Number) totalAmount).doubleValue() : 0);
        dto.setStripePaymentId((String) data.get("stripePaymentId"));
        Object purchasedAt = data.get("purchasedAt");
        if (purchasedAt instanceof com.google.cloud.Timestamp) {
            dto.setPurchasedAt(((com.google.cloud.Timestamp) purchasedAt).toDate().toInstant().toString());
        } else {
            dto.setPurchasedAt((String) purchasedAt);
        }
        return dto;
    }

    // ==================== PRODUCT SALES ====================

    public ProductSaleDTO saveProductSale(ProductSaleDTO sale) throws ExecutionException, InterruptedException {
        String saleId = UUID.randomUUID().toString();
        sale.setSaleId(saleId);
        if (sale.getPurchasedAt() == null) {
            sale.setPurchasedAt(Instant.now().toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("saleId", sale.getSaleId());
        data.put("userId", sale.getUserId());
        data.put("userName", sale.getUserName());
        data.put("userEmail", sale.getUserEmail());
        data.put("totalAmount", sale.getTotalAmount());
        data.put("stripePaymentId", sale.getStripePaymentId());
        data.put("purchasedAt", sale.getPurchasedAt());

        // Convert items
        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (sale.getItems() != null) {
            for (ProductSaleDTO.SaleItem item : sale.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productId", item.getProductId());
                itemMap.put("productName", item.getProductName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemsList.add(itemMap);
            }
        }
        data.put("items", itemsList);

        firestore.collection(PRODUCT_SALES_COLLECTION).document(saleId).set(data).get();
        return sale;
    }

    @SuppressWarnings("unchecked")
    public List<ProductSaleDTO> getAllProductSales() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(PRODUCT_SALES_COLLECTION)
                .get().get().getDocuments();

        return docs.stream().map(this::docToProductSale)
                .sorted((a, b) -> {
                    String dateA = a.getPurchasedAt() != null ? a.getPurchasedAt() : "";
                    String dateB = b.getPurchasedAt() != null ? b.getPurchasedAt() : "";
                    return dateB.compareTo(dateA); // DESC
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private ProductSaleDTO docToProductSale(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        ProductSaleDTO dto = new ProductSaleDTO();
        dto.setSaleId((String) data.get("saleId"));
        dto.setUserId((String) data.get("userId"));
        dto.setUserName((String) data.get("userName"));
        dto.setUserEmail((String) data.get("userEmail"));
        Object totalAmount = data.get("totalAmount");
        dto.setTotalAmount(totalAmount instanceof Number ? ((Number) totalAmount).doubleValue() : 0);
        dto.setStripePaymentId((String) data.get("stripePaymentId"));
        Object purchasedAt = data.get("purchasedAt");
        if (purchasedAt instanceof com.google.cloud.Timestamp) {
            dto.setPurchasedAt(((com.google.cloud.Timestamp) purchasedAt).toDate().toInstant().toString());
        } else {
            dto.setPurchasedAt((String) purchasedAt);
        }

        // Convert items
        List<ProductSaleDTO.SaleItem> items = new ArrayList<>();
        Object itemsObj = data.get("items");
        if (itemsObj instanceof List) {
            for (Object item : (List<?>) itemsObj) {
                if (item instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    ProductSaleDTO.SaleItem saleItem = new ProductSaleDTO.SaleItem();
                    saleItem.setProductId((String) map.get("productId"));
                    saleItem.setProductName((String) map.get("productName"));
                    Object qty = map.get("quantity");
                    saleItem.setQuantity(qty instanceof Number ? ((Number) qty).intValue() : 0);
                    Object price = map.get("price");
                    saleItem.setPrice(price instanceof Number ? ((Number) price).doubleValue() : 0);
                    items.add(saleItem);
                }
            }
        }
        dto.setItems(items);

        return dto;
    }
}
