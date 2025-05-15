package com.scenic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityHeadersInterceptor())
               .addPathPatterns("/**")
               .order(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
    }

    private static class SecurityHeadersInterceptor extends HandlerInterceptorAdapter {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            response.addHeader("X-Content-Type-Options", "nosniff");
            response.addHeader("X-XSS-Protection", "1; mode=block");
            response.addHeader("X-Frame-Options", "DENY");
            response.addHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            StringBuilder csp = new StringBuilder();
            csp.append("default-src 'self'; ");
            csp.append("script-src 'self' https://cdn.bootcdn.net 'unsafe-inline' 'unsafe-eval'; ");
            csp.append("style-src 'self' https://cdn.bootcdn.net 'unsafe-inline'; ");
            csp.append("img-src 'self' data: https:; ");
            csp.append("font-src 'self' https://cdn.bootcdn.net; ");
            csp.append("connect-src 'self'");
            
            response.addHeader("Content-Security-Policy", csp.toString());
            
            String origin = request.getHeader("Origin");
            if (origin != null) {
                response.addHeader("Access-Control-Allow-Origin", origin);
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            }
            
            return true;
        }
    }
} 