package com.server.chat.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JpaUserDetailsService;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.SecurityUtil;
import com.server.chat.entity.ChatRoom;
import com.server.chat.repository.ChatRoomRepository;
import com.server.chat.service.ChatService;
import com.server.global.exception.businessexception.chatexception.ChatNotValidException;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.server.auth.util.AuthConstant.CLAIM_AUTHORITY;
import static com.server.auth.util.AuthConstant.CLAIM_ID;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatRoomRepository chatRoomRepository;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final ChatService chatService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public StompHandler(ChatRoomRepository chatRoomRepository, JpaUserDetailsService jpaUserDetailsService, ChatService chatService, JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.chatRoomRepository = chatRoomRepository;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.chatService = chatService;
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            Claims claims = jwtProvider.getClaims(jwtToken.replace("Bearer ", ""));
            setAuthenticationToContext(claims);
            MDC.put("email", claims.getSubject());
            String authority = (String) claims.get(CLAIM_AUTHORITY);
            chatRoomRepository.setSessionId((String) message.getHeaders().get("simpSessionId"), claims.getSubject() + "," + authority);
        }

        if(StompCommand.SUBSCRIBE == accessor.getCommand()) {

            setAuthenticationFrom(message);

            String roomId = getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            if(!SecurityUtil.getEmail().equals(roomId)) {
                if(!SecurityUtil.isAdmin()) {
                    throw new ChatNotValidException();
                }
            }

            if(SecurityUtil.isAdmin() && !roomId.equals(SecurityUtil.getEmail())) {
                chatService.assignAdmin(SecurityUtil.getEmail(), roomId);
            }else {
                chatService.createChatRoom(roomId);
            }


        } else if(StompCommand.SEND == accessor.getCommand()) {

            setAuthenticationFrom(message);

            String roomId = objectMapper.readValue((byte[]) message.getPayload(), ChatRoom.class).getRoomId();

            ChatRoom chatRoom = chatService.getChatRoom(roomId);

            if(!chatRoom.getRoomId().equals(SecurityUtil.getEmail())) {
                if(!chatRoom.getAdminEmail().equals(SecurityUtil.getEmail())) {
                    throw new ChatNotValidException();
                }
            }
        }

        return message;
    }

    private void setAuthenticationFrom(Message<?> message) {
        String emailAndAuthority = chatRoomRepository.getEmailFrom((String) message.getHeaders().get("simpSessionId"));

        String[] split = emailAndAuthority.split(",");

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(split[1]);

        UserDetails userDetails = new CustomUserDetails(null, split[0], "", Collections.singleton(grantedAuthority));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MDC.put("email", split[1]);
    }

    private String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if(lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            throw new ChatNotValidException();
        }
    }

    private void setAuthenticationToContext(Claims claims) {

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
