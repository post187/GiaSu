package Config.Jwt;

import User.DTO.Response.UserResponse;
import User.Repository.UserRepository;
import User.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(JwtConstant.JWT_HEADER);
        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            try {
                email = jwtUtil.getEmail(jwt);
            } catch (IllegalArgumentException e) {
                log.info("Illegal Argument while fetching the username !!");
            } catch (ExpiredJwtException e) {
                log.info("Given jwt token is expired !!");
            } catch (MalformedJwtException e) {
                log.info("Invalid Token: Some changes have been done in token !!");
            } catch (Exception e) {
                log.error("Error processing JWT token", e);
            }
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserResponse userResponse = userService.findByEmail(email);

            boolean validate = jwtUtil.validateToken(jwt, true);

            if (validate) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userResponse, null, List.of(new SimpleGrantedAuthority("ROLE_" + userResponse.getRoles())));
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else if (email == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            filterChain.doFilter(request, response);
        }

    }
}
