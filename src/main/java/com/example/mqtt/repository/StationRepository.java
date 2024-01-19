package com.example.mqtt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mqtt.entity.StationEntity;

import java.util.List;



public interface StationRepository extends JpaRepository<StationEntity, Long>{
    List<StationEntity> findByStationId(String stationId);
    Boolean existsByStationId(String stationId);
}