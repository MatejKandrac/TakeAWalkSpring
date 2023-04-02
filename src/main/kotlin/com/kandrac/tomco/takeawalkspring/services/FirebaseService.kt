package com.kandrac.tomco.takeawalkspring.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.stereotype.Service
import java.io.FileInputStream
import javax.annotation.PostConstruct


@Service
class FirebaseService {

    @PostConstruct
    fun initialize() {
        try {
            println("INITIALIZING FIREBASE")
            val key = FileInputStream(System.getenv("GOOGLE_SERVICE_KEY"))
            val storageBucket = System.getenv("FIREBASE_STORAGE_BUCKET")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(key))
                .setStorageBucket(storageBucket)
                .build()
            FirebaseApp.initializeApp(options)

        } catch (e: Exception) {
            println("FAILED TO INITIALIZE FIREBASE")
            e.printStackTrace()
        }
    }

}