package com.sku_sku.MatchPrediction.config;


import com.sku_sku.MatchPrediction.security.CustomAuthorizationRequestResolver;
import com.sku_sku.MatchPrediction.security.JwtAuthenticationFilter;
import com.sku_sku.MatchPrediction.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // cors 등록
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestResolver(customAuthorizationRequestResolver)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/students/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> // Security에서 인증과 권환 관련 예외를 직접 처리하는 블록
                        exception.authenticationEntryPoint((request, response, authException) -> { // 인증이 필요하지만 인응이 없는 요청이 들어왔을 때 호출
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP StatusCode 401로 설정
                            response.setContentType("application/json;charset=UTF-8"); // 응답 타입 JSON으로 설정 // REST API 표준에서는 JSON 형태로 에러 응답을 보내는 것이 관례
                            response.getWriter().write("{\"error message\": \"인증 필요\"}"); // JSON 형태로 예외 메시지 response에 반환
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://solvit-final.sku-sku.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}