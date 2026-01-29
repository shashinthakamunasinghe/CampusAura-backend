package com.example.campusaura.security;

import com.example.campusaura.model.entity.User;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Firebase Authentication Filter that integrates with Spring Security.
 * Validates Firebase ID tokens, syncs user to Firestore, and sets authentication in SecurityContext.
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

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

        // Sync user to Firestore (create if first login, fetch if existing)
        User user = userService.getOrCreateUser(
            decodedToken.getUid(),
            decodedToken.getEmail(),
            decodedToken.getName()
        );

        // Create custom principal with user details
        FirebasePrincipal principal = new FirebasePrincipal(
            user.getUid(),
            user.getEmail(),
            user.getName(),
            decodedToken.getClaims()
        );

        // Use role from Firestore (source of truth for user data)
        String role = user.getRole();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role)
        );

        // Create authentication token and set in SecurityContext
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
            );

        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Authenticated user: {} with role: {}", user.getUid(), role);
        // Set request attributes
        request.setAttribute("uid", decodedToken.getUid());
        request.setAttribute("email", decodedToken.getEmail());

        // Create authentication token for Spring Security
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                decodedToken.getUid(),
                null,
                new ArrayList<>()
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in Spring Security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        logger.error("Firebase token validation failed: {}", e.getMessage());
        // Set status and let Spring Security handle the response
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired token\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
