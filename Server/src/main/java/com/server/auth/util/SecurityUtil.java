package com.server.auth.util;

import com.server.auth.jwt.service.CustomUserDetails;
import com.server.domain.member.entity.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;

public class SecurityUtil {

    public static String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getPrincipal() == null) {
            return "미로그인 사용자";
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }

    public static boolean isAdmin() {

        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            return false;
        }

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Authority.valueOf(principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining())).equals(Authority.ROLE_ADMIN);
    }
}
