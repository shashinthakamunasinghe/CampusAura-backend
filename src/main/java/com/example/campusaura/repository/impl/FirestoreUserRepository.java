package com.example.campusaura.repository.impl;

import com.example.campusaura.model.entity.User;
import com.example.campusaura.repository.UserRepository;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Firestore implementation of UserRepository.
 * Handles all Firestore operations for User entity.
 *
 * Collection: users
 * Document ID: Firebase UID (1:1 mapping)
 */
@Repository
public class FirestoreUserRepository implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(FirestoreUserRepository.class);
    private static final String COLLECTION = "users";

    private final Firestore firestore;

    public FirestoreUserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void save(User user) {
        try {
            firestore.collection(COLLECTION)
                    .document(user.getUid())
                    .set(user);

            logger.debug("User saved to Firestore: {}", user.getUid());
        } catch (Exception e) {
            logger.error("Failed to save user: {}", user.getUid(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findByUid(String uid) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(uid);
            var snapshot = docRef.get().get();

            if (!snapshot.exists()) {
                logger.debug("User not found in Firestore: {}", uid);
                return Optional.empty();
            }

            User user = snapshot.toObject(User.class);
            logger.debug("User found in Firestore: {}", uid);
            return Optional.ofNullable(user);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while fetching user: {}", uid, e);
            throw new RuntimeException("Failed to fetch user", e);
        } catch (ExecutionException e) {
            logger.error("Failed to fetch user: {}", uid, e);
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    @Override
    public boolean existsByUid(String uid) {
        try {
            boolean exists = firestore.collection(COLLECTION)
                    .document(uid)
                    .get()
                    .get()
                    .exists();

            logger.debug("User existence check for {}: {}", uid, exists);
            return exists;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while checking user existence: {}", uid, e);
            throw new RuntimeException("Failed to check user existence", e);
        } catch (ExecutionException e) {
            logger.error("Failed to check user existence: {}", uid, e);
            throw new RuntimeException("Failed to check user existence", e);
        }
    }
}
