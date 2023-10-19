package com.server.chat.controller;

import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.SecurityUtil;
import com.server.chat.entity.ChatMessage;
import com.server.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.auth.util.AuthConstant.CLAIM_AUTHORITY;
import static com.server.auth.util.AuthConstant.CLAIM_ID;

@RestController
public class ChatMessageController {

    private final ChatService chatService;
    private final JwtProvider jwtProvider;

    public ChatMessageController(ChatService chatService, JwtProvider jwtProvider) {
        this.chatService = chatService;
        this.jwtProvider = jwtProvider;
    }

    @MessageMapping("/message")
    public void message(@RequestBody ChatMessage message, @Header("Authorization") String token) {

        setAuthenticationToContext(token);

        message.setSender(SecurityUtil.getEmail());

        if(SecurityUtil.isAdmin()) {
            message.setSender("상담원");
        }

        chatService.sendChatMessage(message);

    }

    private void setAuthenticationToContext(String token) {

        Claims claims = jwtProvider.getClaims(token.replace("Bearer ", ""));

        Collection<? extends GrantedAuthority> authorities = getRoles(claims);

        CustomUserDetails principal =
                new CustomUserDetails(claims.get(CLAIM_ID, Long.class), claims.getSubject(), "", authorities);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<SimpleGrantedAuthority> getRoles(Claims claims) {
        return Arrays.stream(claims.get(CLAIM_AUTHORITY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
