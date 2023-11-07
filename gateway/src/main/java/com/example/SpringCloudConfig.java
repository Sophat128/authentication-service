//package com.example;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//@Configuration
//public class SpringCloudConfig {
//    @Bean
//    public RouteLocator buildRouteLocator(RouteLocatorBuilder builder){
//       return builder
//               .routes()
//               .route(r -> r.path("/client-service/v3/api-docs").and().method(HttpMethod.GET).uri("lb://client-service"))
//               .route(r -> r.path("/api/v1/auth/clients/**").and().method(HttpMethod.GET).uri("lb://client-service"))
//               .route(r -> r.path("/api/v1/webpush/v3/api-docs").and().method(HttpMethod.GET).uri("lb://web-push-service"))
//               .route(r -> r.path("/api/v1/webpush/**").and().method(HttpMethod.GET).uri("lb://web-push-service"))
//               .route(r -> r.path("/api/v1/transaction/v3/api-docs").and().method(HttpMethod.GET).uri("lb://transaction-service"))
//               .route(r -> r.path("/api/v1/transaction/**").and().method(HttpMethod.GET).uri("lb://transaction-service"))
//
//               .build();
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        final org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allows all origin
//        config.setAllowedHeaders(
//                Arrays.asList(
//                        "X-Requested-With",
//                        "Origin",
//                        "Content-Type",
//                        "Accept",
//                        "Authorization",
//                        "Access-Control-Allow-Credentials",
//                        "Access-Control-Allow-Headers",
//                        "Access-Control-Allow-Methods",
//                        "Access-Control-Allow-Origin",
//                        "Access-Control-Expose-Headers",
//                        "Access-Control-Max-Age",
//                        "Access-Control-Request-Headers",
//                        "Access-Control-Request-Method",
//                        "Age",
//                        "Allow",
//                        "Alternates",
//                        "Content-Range",
//                        "Content-Disposition",
//                        "Content-Description"
//                )
//        );
//        config.setAllowedMethods(
//                Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
//        );
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//}
