package com.example.mqtt.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class Loop  {

    @Autowired
    private RestTemplate restTemplate;

    public Loop(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 50000) // Gọi mỗi 50 giây
    public void callUrl() {
        String url = "https://vnexpress.net/microservice/weather"; // Thay thế URL bằng đường dẫn tới API bạn muốn gọi

        try {
            int statusCode = restTemplate.getForObject(url, Integer.class);
            System.out.println("Status code: " + statusCode);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

