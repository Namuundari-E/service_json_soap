package com.example.profile.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // ШИЙДЭЛ: OPTIONS хүсэлтийг шалгалгүйгээр шууд нэвтрүүлнэ
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        // Таны одоо байгаа SOAP сервис рүү хандаж токен шалгадаг логик энд үргэлжилнэ...
        // Жишээ нь:
        if (token != null && !token.isEmpty()) {
            // boolean isValid = soapClient.validateToken(token);
            // if (isValid) return true;
            return true; // Туршилтын явцад
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}