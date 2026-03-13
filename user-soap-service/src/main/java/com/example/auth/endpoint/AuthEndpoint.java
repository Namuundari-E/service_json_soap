package com.example.auth.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.auth.LoginRequest;
import com.example.auth.LoginResponse;
import com.example.auth.RegisterRequest;
import com.example.auth.RegisterResponse;
import com.example.auth.ValidateTokenRequest;
import com.example.auth.ValidateTokenResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Endpoint
public class AuthEndpoint {
    private static final String NAMESPACE_URI = "http://example.com/auth";

    // Санах ойд хэрэглэгчид болон идэвхтэй токенуудыг хадгалах
    private Map<String, String> users = new HashMap<>();
    private Set<String> activeTokens = new HashSet<>();

    // Конструктор: Сервис асах бүрт туршилт хийх бэлэн хэрэглэгч үүсгэнэ
    public AuthEndpoint() {
        users.put("admin", "123");
        // Туршилт хийхэд хялбар болгох үүднээс "valid-token"-ийг шууд идэвхжүүлэв
        activeTokens.add("valid-token");
    }

    /**
     * REGISTER: Хэрэглэгч шинээр бүртгэх
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegisterRequest")
    @ResponsePayload
    public RegisterResponse register(@RequestPayload RegisterRequest request) {
        users.put(request.getUsername(), request.getPassword());
        
        RegisterResponse res = new RegisterResponse();
        res.setMessage("User " + request.getUsername() + " registered successfully!");
        return res;
    }

    /**
     * LOGIN: Нэвтрэх нэр, нууц үг шалгаж Token олгох
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "LoginRequest")
    @ResponsePayload
    public LoginResponse login(@RequestPayload LoginRequest request) {
        LoginResponse res = new LoginResponse();
        
        String savedPassword = users.get(request.getUsername());
        
        if (savedPassword != null && savedPassword.equals(request.getPassword())) {
            // Санамсаргүй токен үүсгэж хадгалах
            String token = UUID.randomUUID().toString();
            activeTokens.add(token);
            res.setToken(token);
        } else {
            // Нууц үг буруу бол хоосон токен буюу алдаа
            res.setToken("AUTH_FAILED");
        }
        return res;
    }

    /**
     * VALIDATE: JSON Service-ээс ирсэн токен хүчинтэй эсэхийг шалгах
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateTokenRequest")
    @ResponsePayload
    public ValidateTokenResponse validate(@RequestPayload ValidateTokenRequest request) {
        ValidateTokenResponse res = new ValidateTokenResponse();
        
        // Ирсэн токен бидний activeTokens жагсаалтад байгаа эсэхийг шалгах
        boolean isValid = activeTokens.contains(request.getToken());
        res.setIsValid(isValid);
        
        return res;
    }
}