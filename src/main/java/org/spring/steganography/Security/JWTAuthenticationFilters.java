package org.spring.steganography.Security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.spring.steganography.DTO.UserDTO.TokenPayload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JWTAuthenticationFilters extends OncePerRequestFilter {

    private final JWTServices jwtServices;
    private final UserDetailService userDetailService;

    public JWTAuthenticationFilters(JWTServices jwtServices, UserDetailService userDetailService) {
        this.jwtServices = jwtServices;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader =request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token=authHeader.substring(7);
        String email;
        try{
            email=jwtServices.extractEmail(token);
        } catch (Exception e) {
            filterChain.doFilter(request,response);
            return;
        }

        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserPrincipal userDetails=(UserPrincipal) userDetailService.loadUserByUsername(email);
            TokenPayload payload=new TokenPayload(userDetails.getUsername(),userDetails.getTokenVersion());
            if(jwtServices.validateToken(token,payload)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
