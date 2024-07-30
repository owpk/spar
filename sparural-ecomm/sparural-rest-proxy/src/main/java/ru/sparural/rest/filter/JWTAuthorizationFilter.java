package ru.sparural.rest.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sparural.gobals.Constants;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.security.TokenManager;
import ru.sparural.rest.security.UserPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author Vyacheslav Vorobev
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Value("${security.jwt.token-header}")
    private String TOKEN_HEADER;
    @Value("${security.jwt.token-prefix}")
    private String TOKEN_PREFIX;

    /**
     * JWT фильтр запросов пользователей
     * {@link JWTAuthorizationFilter#TOKEN_HEADER}
     * {@link JWTAuthorizationFilter#TOKEN_PREFIX}
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            var clientType = req.getHeader(Constants.CLIENT_TYPE_HEADER_NAME);

            if (clientType == null ||
                    (!clientType.equals(Constants.CLIENT_TYPE_WEB) &&
                     !clientType.equals(Constants.CLIENT_TYPE_MOBILE))) {
                clientType = Constants.CLIENT_TYPE_MOBILE;
                res.setHeader(Constants.CLIENT_TYPE_HEADER_NAME, clientType);
            }

            if (clientType.equals(Constants.CLIENT_TYPE_WEB)) {
                processIfWeb(req, res, chain);
            } else {
                processIfMobile(req, res, chain);
            }
        } catch (SignatureVerificationException e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.toString());
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        var jwt = tokenManager.decodeAndVerifyRawToken(token);
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String user = jwt.getSubject();
            var claims = jwt.getClaims();
            if (user != null) {
                var roleList = claims.get("role").asList(String.class)
                        .stream()
                        .map(RolesConstants::createRoleString)
                        .collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(Long.valueOf(user), user, roleList), null,
                        roleList.stream().map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
            }
        }
        return null;
    }

    private void processIfWeb(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        var cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.ACCESS_TOKEN)) {
                    tryToAuthorize(cookie.getValue(), res, req, chain);
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
        tryToAuthorize(req.getHeader(TOKEN_HEADER), res, req, chain);
    }

    protected void tryToAuthorize(String token, HttpServletResponse res, HttpServletRequest req, FilterChain chain) throws IOException, ServletException {
        try {
            var authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            doAfterSuccessAuthorization((UserPrincipal) authentication.getPrincipal());
            chain.doFilter(req, res);
        } catch (BadCredentialsException | JWTDecodeException | TokenExpiredException | IOException e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            log.error("jwt filter error: {}", e.getMessage());
        }
    }

    protected void doAfterSuccessAuthorization(UserPrincipal userPrincipal) {
        restToKafkaService.createRequestBuilder()
                .withAction("user/lastActivity")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendAsync();
    }
}