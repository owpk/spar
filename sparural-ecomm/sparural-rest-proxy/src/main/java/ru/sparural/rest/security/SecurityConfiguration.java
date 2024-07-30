package ru.sparural.rest.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.sparural.rest.filter.JWTAuthorizationFilter;

import java.nio.charset.StandardCharsets;

/**
 * @author Vyacheslav Vorobev
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTAuthorizationFilter authorizationFilter;

    @Value("${rest.base-url}")
    private String restEndpoint;

    @Value("${rest.version}")
    private String restVersion;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var charEncoding = new CharacterEncodingFilter();
        charEncoding.setEncoding(StandardCharsets.UTF_8.name());
        http
//                .addFilterBefore(charEncoding, ChannelProcessingFilter.class)
                .cors().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().permitAll()
                .and().headers().frameOptions().disable()
                .and()
                .anonymous().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and().requestCache().requestCache(requestCache())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    RequestCache requestCache() {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        cache.setCreateSessionAllowed(false);
        return cache;
    }
}