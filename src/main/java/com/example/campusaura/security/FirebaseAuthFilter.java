package com.example.campusaura.security;

import com.example.campusaura.model.User;
import com.example.campusaura.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Firebase Authentication Filter.
 *
 * PATTERN:
 * - Verifies Firebase ID token
 * - Fetches user role from Firestore (single source of truth)
 * - Creates or syncs user in database
 */
@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthFilter.class);
  private final UserService userService;

  public FirebaseAuthFilter(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    // Skip authentication for public endpoints
    String requestPath = request.getRequestURI();
    if (isPublicEndpoint(requestPath)) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    logger.debug("🔥 Authorization Header: {}", authHeader != null ? "Bearer ***" : "null");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        // Verify Firebase token
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        logger.debug("✅ Token verified for UID: {}", decodedToken.getUid());

        // Get or create user in Firestore (single source of truth for roles)
        User user = userService.getOrCreateUser(
            decodedToken.getUid(),
            decodedToken.getEmail(),
            decodedToken.getName()
        );

        // Use role from Firestore
        String role = user.getRole();
        logger.debug("User role from Firestore: {}", role);
        
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role)
        );

        // Create authentication with UID as principal
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                decodedToken.getUid(),  // UID as principal
                null,
                authorities
            );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // set UID, email, and name as request attributes for registration endpoints
        request.setAttribute("firebaseUid", decodedToken.getUid());
        request.setAttribute("firebaseEmail", decodedToken.getEmail());
        request.setAttribute("firebaseName", decodedToken.getName());

        logger.debug("Authenticated user: {} with role: {}", decodedToken.getUid(), role);

      } catch (Exception e) {
        logger.error("❌ Token verification failed: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or expired token\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Check if the request path is a public endpoint that doesn't require authentication
   */
  private boolean isPublicEndpoint(String path) {
    return path.startsWith("/api/public/") ||
           path.startsWith("/api/events/public/") ||
           path.equals("/api/events/landing-page") ||
           path.equals("/api/events/latest") ||
           path.startsWith("/api/auth/validate-email") ||
           path.startsWith("/api/auth/validate-registration") ||
           path.startsWith("/api/auth/registration-info");
  }
}
