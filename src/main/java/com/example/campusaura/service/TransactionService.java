package com.example.campusaura.service;

import com.example.campusaura.dto.PaymentStatsDTO;
import com.example.campusaura.dto.TransactionResponseDTO;
import com.example.campusaura.model.Transaction;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "transactions";

    // Get all transactions
    public List<TransactionResponseDTO> getAllTransactions() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToTransaction)
                .map(this::transactionToDTO)
                .collect(Collectors.toList());
    }

    // Get recent transactions
    public List<TransactionResponseDTO> getRecentTransactions(int limit) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToTransaction)
                .map(this::transactionToDTO)
                .collect(Collectors.toList());
    }

    // Get payment statistics
    public PaymentStatsDTO getPaymentStats() throws ExecutionException, InterruptedException {
        List<Transaction> allTransactions = getAllTransactionsInternal();
        
        // Calculate ticket revenue
        double ticketRevenue = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.TICKET 
                        && t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Calculate marketplace revenue
        double marketplaceRevenue = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.MARKETPLACE 
                        && t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Get recent transactions
        List<TransactionResponseDTO> recentTransactions = getRecentTransactions(10);
        
        return new PaymentStatsDTO(ticketRevenue, marketplaceRevenue, recentTransactions);
    }

    // Get revenue by type
    public double getRevenueByType(Transaction.TransactionType type) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("type", type.toString())
                .whereEqualTo("status", Transaction.TransactionStatus.COMPLETED.toString());
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToTransaction)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Helper methods
    private List<Transaction> getAllTransactionsInternal() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToTransaction)
                .collect(Collectors.toList());
    }

    private Transaction documentToTransaction(DocumentSnapshot document) {
        Transaction transaction = new Transaction();
        transaction.setId(document.getId());
        
        String typeStr = document.getString("type");
        if (typeStr != null) {
            transaction.setType(Transaction.TransactionType.valueOf(typeStr));
        }
        
        transaction.setUserId(document.getString("userId"));
        transaction.setUserName(document.getString("userName"));
        transaction.setEventId(document.getString("eventId"));
        transaction.setEventName(document.getString("eventName"));
        transaction.setProductId(document.getString("productId"));
        transaction.setProductName(document.getString("productName"));
        
        Double amount = document.getDouble("amount");
        if (amount != null) {
            transaction.setAmount(amount);
        }
        
        transaction.setPaymentMethod(document.getString("paymentMethod"));
        
        String statusStr = document.getString("status");
        if (statusStr != null) {
            transaction.setStatus(Transaction.TransactionStatus.valueOf(statusStr));
        }
        
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            transaction.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }
        
        String completedAtStr = document.getString("completedAt");
        if (completedAtStr != null) {
            transaction.setCompletedAt(LocalDateTime.parse(completedAtStr));
        }
        
        return transaction;
    }

    private TransactionResponseDTO transactionToDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setUserId(transaction.getUserId());
        dto.setUserName(transaction.getUserName());
        dto.setEventId(transaction.getEventId());
        dto.setEventName(transaction.getEventName());
        dto.setProductId(transaction.getProductId());
        dto.setProductName(transaction.getProductName());
        dto.setAmount(transaction.getAmount());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setCompletedAt(transaction.getCompletedAt());
        return dto;
    }
}
