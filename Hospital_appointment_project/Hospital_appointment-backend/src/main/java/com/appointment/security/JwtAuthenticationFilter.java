// package com.appointment.security;



// import java.io.IOException;

// import jakarta.servlet.*;
// import jakarta.servlet.http.*;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.*;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private CustomUserDetailsService userDetailsService;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain)
//             throws ServletException, IOException {

//         final String authHeader = request.getHeader("Authorization");
        
//         System.out.println("Authorization header = " + authHeader);

//         String token = null;
//         String username = null;

//         if (authHeader != null && authHeader.startsWith("Bearer ")) {

//             token = authHeader.substring(7);
//             try {
//                 username = jwtUtil.extractUsername(token);
//             } catch (Exception e) {
//             	logger.warn("Invalid JWT token: " + e.getMessage());
//             }

//         }

//         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

//             UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//             if (jwtUtil.validateToken(token, username)) {

//                 UsernamePasswordAuthenticationToken authenticationToken =
//                         new UsernamePasswordAuthenticationToken(
//                                 userDetails,
//                                 null,
//                                 userDetails.getAuthorities()
//                         );

//                 authenticationToken.setDetails(
//                         new WebAuthenticationDetailsSource().buildDetails(request)
//                 );

//                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//             }
//         }

//         filterChain.doFilter(request, response);
//     }
// }

package com.appointment.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ Allow preflight (CORS)
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization header = " + authHeader);

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        // ✅ MAIN FIX: Set role manually from JWT
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(token, username)) {

                // 🔥 Extract role from JWT
                String role = jwtUtil.extractRole(token);

                System.out.println("ROLE FROM TOKEN = " + role);

                // 🔥 Convert to authority
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}