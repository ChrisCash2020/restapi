package com.restaurant.restapi;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestapiApplication {

	public static void main(String[] args) throws IOException {
		FileInputStream serviceAccount = new FileInputStream("src/food-1c80d-firebase-adminsdk-ysiwu-f9669aed7d.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).setStorageBucket("food-1c80d.appspot.com")
				.build();

		FirebaseApp.initializeApp(options);
		SpringApplication.run(RestapiApplication.class, args);
	}

}
