package com.example.mqtt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.mqtt.dto.SensorDTO;
import com.example.mqtt.entity.SensorEntity;
import com.example.mqtt.entity.StationEntity;
import com.example.mqtt.exception.ResourceNotFoundException;
import com.example.mqtt.repository.StationRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@Service
public class SensorService {

    private StationRepository stationRepository;

    @Autowired
    SensorService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public void saveSensorData(String station_id, SensorDTO sensorDTO) throws InterruptedException, ExecutionException {
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference("server/data/sensor-data/" + station_id + "/" + sensorDTO.getSensor_id());
        ref.push().setValueAsync(sensorDTO);
    }

    public CompletableFuture<SensorDTO> getSensorDataLastest(String Id, String topic)
            throws InterruptedException, ExecutionException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/data/sensor-data/" + Id + "/" + topic);
        CompletableFuture<SensorDTO> future = new CompletableFuture<>();
        ref.orderByChild("created_at").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                SensorDTO lastestData = dataSnapshot.getValue(SensorDTO.class);
                future.complete(lastestData);
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onCancelled'");
            }

            @Override
            public void onChildChanged(DataSnapshot arg0, String arg1) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onChildChanged'");
            }

            @Override
            public void onChildMoved(DataSnapshot arg0, String arg1) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onChildMoved'");
            }

            @Override
            public void onChildRemoved(DataSnapshot arg0) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onChildRemoved'");
            }
        });
        return future;
    }

    public ResponseEntity<CompletableFuture<List<SensorDTO>>> getSensorDataByTime(String station_id, String topic,
            int num, String startDateTime, String endDateTime) {
        List<StationEntity> stationEntities = stationRepository.findByStationId(station_id);
        if (stationEntities.isEmpty()) {
            throw new ResourceNotFoundException("Station", "Station_id", station_id);
        }
        Boolean checkSensorExists = false;
        for(SensorEntity sensorEntity : stationEntities.get(0).getSensors()) {
            if(sensorEntity.getSensorid().equals(topic)) {
                checkSensorExists = true;
                break;
            }
        }
        if(!checkSensorExists) {
            throw new ResourceNotFoundException("Sensor", "Sensor id", topic);
        }
        final CompletableFuture<List<SensorDTO>> future = new CompletableFuture<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/data/sensor-data/" + station_id + "/" + topic);

        Query query = ref.orderByChild("created_at").limitToLast(num).startAt(startDateTime).endAt(endDateTime);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<SensorDTO> sensorList = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    SensorDTO sensor = childSnapshot.getValue(SensorDTO.class);
                    sensor.setCreated_at(sensor.getCreated_at());
                    sensorList.add(sensor);
                }

                future.complete(sensorList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return ResponseEntity.ok(future);
    }
}
