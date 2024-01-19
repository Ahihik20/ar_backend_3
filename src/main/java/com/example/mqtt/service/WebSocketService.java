package com.example.mqtt.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.mqtt.dto.ErrorDetails;
import com.example.mqtt.dto.SensorDTO;
import com.example.mqtt.entity.RoomEntity;
import com.example.mqtt.entity.SensorEntity;
import com.example.mqtt.entity.StationEntity;
import com.example.mqtt.exception.ResourceNotFoundException;
import com.example.mqtt.repository.StationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketService extends TextWebSocketHandler {
    @Autowired
    private Map<String, RoomEntity> roomConnections = new HashMap<>();

    @Autowired
    private SensorService sensorService;

    private ObjectMapper objectMapper;
    private StationRepository stationRepository;

    @Autowired
    WebSocketService(ObjectMapper objectMapper, StationRepository stationRepository, SensorService sensorService) {
        this.objectMapper = objectMapper;
        this.stationRepository = stationRepository;
        this.sensorService = sensorService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String queryString = session.getUri().getRawQuery();
            if (queryString == null || queryString.isEmpty()) {
                session.sendMessage(new TextMessage("Lacks of query in URL"));
                session.close();
                return;
            }
            Map<String, String> queryHashMap = splitQueryStringToHashMap(queryString);
            List<StationEntity> stationEntity = stationRepository.findByStationId(queryHashMap.get("stationId"));
            if (stationEntity.size() == 0) {
                session.sendMessage(new TextMessage("Station " + queryHashMap.get("stationId") + " not found"));
                session.close();
                return;
            }
            RoomEntity roomConnect = roomConnections.get(queryHashMap.get("stationId"));
            if (roomConnect == null) {
                roomConnect = new RoomEntity(queryHashMap.get("stationId"));
                roomConnections.put(queryHashMap.get("stationId"), roomConnect);
            }
            roomConnect.addClient(session);
            // session.sendMessage(new TextMessage("Connect successfully with station " + queryHashMap.get("stationId")));
            String sensorName = getSensorName(queryHashMap.get("stationId"));
            senDataLastedByTopic(session, new TextMessage(sensorName));
        } catch (ResourceNotFoundException exception) {
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Xử lý sự kiện khi client đóng kết nối websocket
        super.afterConnectionClosed(session, status);
        String queryString = session.getUri().getRawQuery();
        Map<String, String> queryHashMap = splitQueryStringToHashMap(queryString);
        RoomEntity roomConnect = roomConnections.get(queryHashMap.get("stationId"));
        if (roomConnect == null) {
            return;
        } else {
            roomConnect.removeClient(session);
        }
    }

    public void sendMessageToAllClient(String id, String message) {
        RoomEntity roomConnect = roomConnections.get(id);
        if (roomConnect != null) {
            roomConnect.sendMessageToAll(message);
        }
    }

    public Map<String, String> splitQueryStringToHashMap(String queryString) {
        Map<String, String> resultMap = new HashMap<>();
        String[] pairs = queryString.split("\\?");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                try {
                    key = URLDecoder.decode(key, "UTF-8");
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    public String getSensorName(String StationId) {
        List<StationEntity> stationEntity = stationRepository.findByStationId(StationId);
        Set<SensorEntity> sensorEntities = stationEntity.get(0).getSensors();
        StringBuilder result = new StringBuilder();
        for (SensorEntity sensorEntity : sensorEntities) {
            result.append(sensorEntity.getSensorid()).append(",");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public void senDataLastedByTopic(WebSocketSession session, TextMessage message) throws IOException {
        if (!session.isOpen()) {
            session.sendMessage(new TextMessage("Session is null"));
            session.close();
        }
        String queryString = session.getUri().getRawQuery();
        if (queryString == null || queryString.isEmpty()) {
            session.sendMessage(new TextMessage("Lacks of query in URL"));
            session.close();
        }
        Map<String, String> queryHashMap = splitQueryStringToHashMap(queryString);

        if (message.getPayload().equals("")) {
            session.sendMessage(new TextMessage("Topic is empty"));
            return;
        }
        List<String> topics = Arrays.asList(message.getPayload().split(","));
        topics.stream().forEach(
                topic -> {
                    try {
                        CompletableFuture<SensorDTO> latestedData = sensorService
                                .getSensorDataLastest(queryHashMap.get("stationId"), topic);
                        SensorDTO lastedDTO = latestedData.get();
                        lastedDTO.setCreated_at(lastedDTO.getCreated_at());
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json = objectMapper.writeValueAsString(lastedDTO);
                        ConcurrentWebSocketSessionDecorator concurrentSession = new ConcurrentWebSocketSessionDecorator(
                                session, 5000, 8192);
                        concurrentSession.sendMessage(new TextMessage(json));
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
