package com.uok.ucms_backend.auth.util;

import com.uok.ucms_backend.auth.service.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    
    /**
     * Get the current authenticated user's ID from security context
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal) {
            UserDetailsServiceImpl.UserPrincipal userPrincipal = 
                (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        
        throw new RuntimeException("No authenticated user found");
    }
    
    /**
     * Get the current authenticated user's username from security context
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal) {
            UserDetailsServiceImpl.UserPrincipal userPrincipal = 
                (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getUsername();
        }
        
        throw new RuntimeException("No authenticated user found");
    }
    
    /**
     * Get the current authenticated user's email from security context
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal) {
            UserDetailsServiceImpl.UserPrincipal userPrincipal = 
                (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getEmail();
        }
        
        throw new RuntimeException("No authenticated user found");
    }
    
    /**
     * Check if there is an authenticated user
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal;
    }
}
