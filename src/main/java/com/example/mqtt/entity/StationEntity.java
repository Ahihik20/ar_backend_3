package com.example.mqtt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "station")
public class StationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "station_id", nullable = false)
    private String stationId;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "gps_longitude", nullable = false)
    private Double gpsLongitude;

    @Column(name = "gps_latitude", nullable = false)
    private Double gpsLatitude;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "station_sensor", joinColumns = @JoinColumn(name = "station_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "sensor_id", referencedColumnName = "id"))
    private Set<SensorEntity> sensors;
}
