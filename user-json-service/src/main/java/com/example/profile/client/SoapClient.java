package com.example.profile.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class SoapClient {

    public boolean validateToken(String token) {
        String url = "http://localhost:8081/ws";
        
        // SOAP XML Body бэлдэх
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                     "xmlns:auth=\"http://example.com/auth\">" +
                     "<soapenv:Header/><soapenv:Body>" +
                     "<auth:ValidateTokenRequest><auth:token>" + token + "</auth:token></auth:ValidateTokenRequest>" +
                     "</soapenv:Body></soapenv:Envelope>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> entity = new HttpEntity<>(xml, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.postForObject(url, entity, String.class);
            return response != null && response.contains("<isValid>true</isValid>");
        } catch (Exception e) {
            return false;
        }
    }
}