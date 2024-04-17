package com.komsije.booking;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;

import java.io.IOException;

@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) throws FirebaseMessagingException, IOException {
		SpringApplication.run(BookingApplication.class, args);
//		FirebaseApp.initializeApp(new FirebaseOptions.Builder().setProjectId("booking-team9").setCredentials(GoogleCredentials.getApplicationDefault()).build());

	}


}
