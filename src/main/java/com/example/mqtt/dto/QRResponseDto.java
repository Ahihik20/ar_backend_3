package com.example.mqtt.dto;

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
public class QRResponseDto {
    private String qrCodeBase64;
    private String id;

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public String getId() {
        return id;
    }
}
