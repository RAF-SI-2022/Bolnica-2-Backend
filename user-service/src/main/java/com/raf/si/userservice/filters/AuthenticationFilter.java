package com.raf.si.userservice.filters;

import com.google.gson.Gson;
import com.raf.si.userservice.exception.ErrorCode;
import com.raf.si.userservice.exception.ErrorDetails;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.JwtUtil;
import com.raf.si.userservice.utils.TokenPayload;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Gson gson;
    private final UserService userService;

    public AuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.gson = new Gson();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token nije obezbedjen");
            handleError("Token nije obezbedjen", response);
            return;
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractAllClaims(token);

        if (claims == null) {
            log.warn("Token invalidan: '{}'", token);
            handleError("Token invalidan", response);
            return;
        }

        if (jwtUtil.isTokenExpired(token)) {
            log.warn("Token expired: '{}'", token);
            handleError("Token je istekao", response);
            return;
        }

        if (!userService.userExistsByLbzAndIsDeleted(UUID.fromString(claims.getSubject()))) {
            log.warn("Korisnik ne postoji sa tokenom: '{}'", token);
            handleError("Token je invalidan", response);
            return;
        }
        TokenPayload userDetails = setPayload(claims);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, setAuthorities(userDetails.getPermissions()));

        usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        String path = request.getRequestURI();
        return matcher.match("/**/auth/**", path)
                || matcher.match("/**/v3/api-docs/**", path)
                || matcher.match("/**/swagger-resources/**", path)
                || matcher.match("/**/swagger-ui/**", path)
                || matcher.match("/**/users/reset-password", path)
                || matcher.match("/**/users/update-password", path);
    }

    private List<SimpleGrantedAuthority> setAuthorities(List<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private void handleError(String message, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter()
                .write(gson.toJson(new ErrorDetails(ErrorCode.UNAUTHORIZED, message, Instant.now().toString())));
    }

    @SuppressWarnings({"unchecked"})
    private TokenPayload setPayload(Claims claims) {
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString(claims.getSubject()));
        tokenPayload.setFirstName((String) claims.get("firstName"));
        tokenPayload.setLastName((String) claims.get("lastName"));
        tokenPayload.setTitle(Title.valueOfNotation((String) claims.get("title")));
        tokenPayload.setProfession(Profession.valueOfNotation((String) claims.get("profession")));
        tokenPayload.setPbo(UUID.fromString((String) claims.get("pbo")));
        tokenPayload.setDepartmentName((String) claims.get("departmentName"));
        tokenPayload.setPbb(UUID.fromString((String) claims.get("pbb")));
        tokenPayload.setHospitalName((String) claims.get("hospitalName"));
        List<String> permissions = ((List<String>) claims.get("permissions"));
        tokenPayload.setPermissions(permissions);
        tokenPayload.setCovidAccess((Boolean) claims.get("covidAccess"));

        return tokenPayload;
    }
}
