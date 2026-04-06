package com.example.campusaura.service;

import com.example.campusaura.dto.DashboardStatsDTO;
import com.example.campusaura.dto.EventResponseDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DashboardService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private EventService eventService;

    @Autowired
    private ProductService productService;

    // Get dashboard statistics
    public DashboardStatsDTO getDashboardStats() throws ExecutionException, InterruptedException {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Get total events count
        long totalEvents = getCollectionCount("events");
        stats.setTotalEvents(totalEvents);

        // Get total active users count
        long activeUsers = getCollectionCount("users");
        stats.setActiveUsers(activeUsers);

        // Get total products count
        long totalProducts = getCollectionCount("products");
        stats.setTotalProducts(totalProducts);

        // Get products sold count
        long productsSold = productService.getSoldProductsCount();
        stats.setProductsSold(productsSold);

        // Get 5 recent events
        List<EventResponseDTO> recentEvents = eventService.getRecentEvents(5);
        stats.setRecentEvents(recentEvents);

        // TODO: Implement percentage changes calculation
        // stats.setEventsPercentageChange(calculatePercentageChange(...));
        // stats.setUsersPercentageChange(calculatePercentageChange(...));
        // stats.setProductsPercentageChange(calculatePercentageChange(...));
        
        // TODO: Implement top coordinators fetching
        // List<TopCoordinatorDTO> topCoordinators = getTopCoordinators(5);
        // stats.setTopCoordinators(topCoordinators);

        return stats;
    }

    // Helper method to get collection count
    private long getCollectionCount(String collectionName) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
        return future.get().getDocuments().size();
    }
}
