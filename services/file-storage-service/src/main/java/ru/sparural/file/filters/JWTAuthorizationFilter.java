package ru.sparural.file.filters;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sparural.file.security.TokenManager;
import ru.sparural.file.security.UserPrincipal;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private static final String CLIENT_TYPE_HEADER_NAME = "x-client-type";
    private static final String CLIENT_TYPE_WEB = "web";
    private static final String CLIENT_TYPE_MOBILE = "mobile";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    private final TokenManager tokenManager;
    @Value("${security.jwt.token-header}")
    private String TOKEN_HEADER;
    @Value("${security.jwt.token-prefix}")
    private String TOKEN_PREFIX;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var clientType = request.getHeader(CLIENT_TYPE_HEADER_NAME);
        if (clientType == null || (!clientType.equals(CLIENT_TYPE_WEB)
                && !clientType.equals(CLIENT_TYPE_MOBILE))) {
            clientType = CLIENT_TYPE_MOBILE;
            response.setHeader(CLIENT_TYPE_HEADER_NAME, clientType);
        }
        if (clientType.equals(CLIENT_TYPE_WEB)) {
            processIfWeb(request, response, filterChain);
        } else processIfMobile(request, response, filterChain);
    }

    private void processIfWeb(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        var cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_TOKEN)) {
                    tryAuthorize(cookie.getValue(), res, req, chain);
                    return;
                }
            }
        }
        chain.doFilter(req, res);
    }

    private void processIfMobile(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        var header = req.getHeader(TOKEN_HEADER);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        tryAuthorize(req.getHeader(TOKEN_HEADER), res, req, chain);
    }

    private void tryAuthorize(String token, HttpServletResponse res, HttpServletRequest req, FilterChain chain) throws IOException, ServletException {
        try {
            var authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch (BadCredentialsException | JWTDecodeException | TokenExpiredException | IOException e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            log.error("jwt filter error: {}", e.getMessage());
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        var jwt = tokenManager.decodeAndVerifyRawToken(token);
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String user = jwt.getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(Long.valueOf(user), user),
                        null,
                        Collections.emptyList()
                );
            }
        }
        return null;
    }
}
