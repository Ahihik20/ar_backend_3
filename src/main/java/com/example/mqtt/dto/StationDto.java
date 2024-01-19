package com.example.mqtt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StationDto {
    private String station_id;
    private String station_name;
    private Double gps_longitude;
    private Double gps_latitude;
    private String timestamp;
    private List<SensorDTO> sensors;
}