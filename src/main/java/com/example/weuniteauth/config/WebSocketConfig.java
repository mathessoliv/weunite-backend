package com.example.weuniteauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("🔌 Nova tentativa de conexão WebSocket");

                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        try {
                            Jwt jwt = jwtDecoder.decode(token);

                            System.out.println("📝 Token decodificado:");
                            System.out.println("  - Subject: " + jwt.getSubject());

                            String username = jwt.getSubject();
                            if (username == null || username.isEmpty()) {
                                System.err.println("❌ Token sem subject válido");
                                throw new IllegalArgumentException("Token inválido");
                            }

                            // ✅ MÉTODO SEGURO para extrair userId
                            Long userId = extractUserId(jwt);
                            System.out.println("  - User ID extraído: " + userId);

                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.emptyList()
                            );

                            accessor.setUser(auth);

                            if (accessor.getSessionAttributes() != null) {
                                accessor.getSessionAttributes().put("userId", userId);
                                accessor.getSessionAttributes().put("username", username);
                            }

                            System.out.println("✅ WebSocket autenticado: " + username + " (ID: " + userId + ")");

                        } catch (Exception e) {
                            System.err.println("❌ Erro ao validar token: " + e.getMessage());
                            throw new IllegalArgumentException("Autenticação falhou: " + e.getMessage());
                        }
                    } else {
                        System.err.println("❌ Token não fornecido");
                        throw new IllegalArgumentException("Token não fornecido");
                    }
                }

                return message;
            }

            // ✅ MÉTODO AUXILIAR SEGURO para extrair userId
            private Long extractUserId(Jwt jwt) {
                // Tenta userId
                Object userIdClaim = jwt.getClaim("userId");
                if (userIdClaim != null) {
                    return convertToLong(userIdClaim, "userId");
                }

                // Tenta id
                Object idClaim = jwt.getClaim("id");
                if (idClaim != null) {
                    return convertToLong(idClaim, "id");
                }

                // Tenta user_id
                Object userIdSnakeClaim = jwt.getClaim("user_id");
                if (userIdSnakeClaim != null) {
                    return convertToLong(userIdSnakeClaim, "user_id");
                }

                // Tenta parsear o subject como número
                try {
                    return Long.parseLong(jwt.getSubject());
                } catch (Exception e) {
                    System.out.println("⚠️ Subject não é um número");
                }

                System.out.println("⚠️ userId não encontrado no token, usando null");
                return null;
            }

            // ✅ Converte qualquer tipo para Long de forma segura
            private Long convertToLong(Object value, String claimName) {
                try {
                    if (value instanceof Long) {
                        return (Long) value;
                    } else if (value instanceof Integer) {
                        return ((Integer) value).longValue();
                    } else if (value instanceof String) {
                        return Long.parseLong((String) value);
                    } else if (value instanceof Number) {
                        return ((Number) value).longValue();
                    } else {
                        System.out.println("⚠️ Claim '" + claimName + "' tem tipo não esperado: " + value.getClass());
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Erro ao converter claim '" + claimName + "': " + e.getMessage());
                    return null;
                }
            }
        });
    }
}