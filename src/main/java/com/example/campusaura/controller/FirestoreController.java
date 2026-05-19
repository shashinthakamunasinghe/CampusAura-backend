package com.example.campusaura.controller;

import com.example.campusaura.service.FirestoreService;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/firestore")
public class FirestoreController {

    @Autowired
    private FirestoreService firestoreService;

    @PostMapping("/{collection}/{documentId}")
    public ResponseEntity<String> createDocument(
            @PathVariable String collection,
            @PathVariable String documentId,
            @RequestBody Map<String, Object> data) throws ExecutionException, InterruptedException {
        String result = firestoreService.saveDocument(collection, documentId, data);
        return ResponseEntity.ok("Document saved at: " + result);
    }

    @GetMapping("/{collection}/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocument(
            @PathVariable String collection,
            @PathVariable String documentId) throws ExecutionException, InterruptedException {
        Map<String, Object> document = firestoreService.getDocument(collection, documentId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{collection}/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable String collection,
            @PathVariable String documentId) throws ExecutionException, InterruptedException {
        String result = firestoreService.deleteDocument(collection, documentId);
        return ResponseEntity.ok("Document deleted at: " + result);
    }

    @GetMapping("/{collection}")
    public ResponseEntity<List<Map<String, Object>>> getAllDocuments(
            @PathVariable String collection) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestoreService.getAllDocuments(collection);
        
        // Convert QueryDocumentSnapshot objects to Maps for JSON serialization
        List<Map<String, Object>> documentMaps = documents.stream()
            .map(doc -> {
                Map<String, Object> docData = doc.getData();
                // Add document ID to the data map
                docData.put("id", doc.getId());
                return docData;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(documentMaps);
    }
}