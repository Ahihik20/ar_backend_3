package com.example.mqtt.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RoomEntity {
    private String roomName;
    private List<WebSocketSession> clients;

    public RoomEntity(String roomName) {
        this.roomName = roomName;
        this.clients = new ArrayList<>();
    }

    public void addClient(WebSocketSession session) {
        clients.add(session);
    }

    public void removeClient(WebSocketSession session) {
        clients.remove(session);
    }

    public void sendMessageToAll(String message) {
        for (WebSocketSession client : clients) {
            try {
                ConcurrentWebSocketSessionDecorator concurrentSession =
                        new ConcurrentWebSocketSessionDecorator(client, 5000, 8192);
                concurrentSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                // Xử lý lỗi khi gửi tin nhắn tới client
            }
        }
    }
}
