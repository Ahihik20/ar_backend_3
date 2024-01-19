package com.example.mqtt.config;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jakarta.annotation.PostConstruct;

// @Service
@Configuration
public class FireBaseInitialization {
    
    @Value("${firebase.database.url}")
    private String databaseUrl;

    @Value("${firebase.database.accountKey}")
    private String AccountKeyFile;

    @PostConstruct
    public void initialization() {
        FileInputStream serviceAccount;

        try {
            serviceAccount = new FileInputStream(AccountKeyFile);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            FirebaseApp.initializeApp(options);
            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference("/public_resource");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
