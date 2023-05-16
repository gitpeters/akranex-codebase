package com.akraness.akranesswaitlist.service.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration

public class FirebaseConfig {

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException{
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("akranex-dev-firebase-adminsdk-ho749-44e042f983.json").getInputStream());
        FirebaseOptions options = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(options, "my-app");
        return FirebaseMessaging.getInstance(app);
    }
}
