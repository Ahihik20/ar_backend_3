package com.example.mqtt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.mqtt.entity.RoomEntity;
import com.example.mqtt.service.MqttService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MqttApplication {
        @Value("${mqttBroker.serverUrl}")
        private String serverUrl;

        @Bean
        public Map<String, RoomEntity> roomConnections() {
                return new HashMap<>();
        };

        @Bean
        public MqttClient MqttClient() throws MqttException {
                return new MqttClient(serverUrl, MqttClient.generateClientId());
        };

        @Bean
        public ObjectMapper objectMapper() {
                return new ObjectMapper();
        }

        public static void main(String[] args) throws MqttException, IOException {
                ConfigurableApplicationContext context = SpringApplication.run(MqttApplication.class, args);
                MqttService mqttService = context.getBean(MqttService.class);
                mqttService.connect();

        }

        @Bean
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }

        @Bean
        public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
                return new BufferedImageHttpMessageConverter();
        }

        @EventListener(ApplicationReadyEvent.class)
        public void connectAndPublish() throws MqttException {

        }

}
