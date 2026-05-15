package com.example.profile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SoapClient {

    @Value("${soap.service.url:http://localhost:8081/ws}")
    private String soapServiceUrl;

    public boolean validateToken(String token) {
        String escapedToken = token
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");

        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:auth=\"http://example.com/auth\">"
                + "<soapenv:Header/><soapenv:Body>"
                + "<auth:ValidateTokenRequest><auth:token>" + escapedToken + "</auth:token></auth:ValidateTokenRequest>"
                + "</soapenv:Body></soapenv:Envelope>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> entity = new HttpEntity<>(xml, headers);

        try {
            String response = new RestTemplate().postForObject(soapServiceUrl, entity, String.class);
            return response != null && response.matches("(?s).*<[^>]*isValid[^>]*>\\s*true\\s*</[^>]*isValid>.*");
        } catch (Exception e) {
            return false;
        }
    }
}
