package com.example.mqtt.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mqtt.dto.SensorDTO;
import com.example.mqtt.service.SensorService;

@RestController()
@RequestMapping("/api")
public class WebsocketController {

    @Autowired
    private SensorService sensorService;


    WebsocketController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/sensors/{station_id}/{topic}/{num}/{startDateTime}/{endDateTime}")
    public ResponseEntity<CompletableFuture<List<SensorDTO>>> getSensorDataByTime(@PathVariable("station_id") String station_id,
            @PathVariable("topic") String topic, @PathVariable("num") String num, @PathVariable("startDateTime") String startDateTime,
            @PathVariable("endDateTime") String endDateTime) throws InterruptedException,
            ExecutionException {
        ResponseEntity<CompletableFuture<List<SensorDTO>>> futureData = sensorService.getSensorDataByTime(station_id, topic, Integer.parseInt(num), startDateTime,
                endDateTime);
        return futureData;
    }

    @GetMapping("/sensors/{station_id}/{topic}")
    public SensorDTO test(@PathVariable("station_id") String station_id,
            @PathVariable("topic") String topic) throws InterruptedException,
            ExecutionException {
        CompletableFuture<SensorDTO> latestedData = sensorService.getSensorDataLastest(station_id, topic);
        return latestedData.get();
    }

}