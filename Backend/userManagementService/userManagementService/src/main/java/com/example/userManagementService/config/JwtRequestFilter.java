package com.example.userManagementService.config;

import com.example.userManagementService.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        // Excluir rutas de registro y login
        String uri = request.getRequestURI();
        if (uri.equals("/user-Management/player-register")) {
            chain.doFilter(request, response);
            return;
        }

        String userID = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {

                userID = jwtUtil.getIDFromToken(jwt);
                logger.info("Token válido, usuario extraído: " + userID);
            } catch (Exception e) {
                logger.error("Error al extraer el usuario del token", e);
            }
        } else {
            logger.warn("No se encontró un token válido en la cabecera Authorization.");
        }


        if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                // crear un objeto de usuario que contenga más información
                UserDetails userDetails = new CustomUserDetails(userID);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("Usuario autenticado correctamente en el contexto de seguridad");
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            }
        }

        chain.doFilter(request, response);
    }
}