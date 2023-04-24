package com.kandrac.tomco.takeawalkspring.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileInputStream
import javax.annotation.PostConstruct


@Service
class FirebaseService {

    private val logger = LoggerFactory.getLogger(FirebaseService::class.java)

    @PostConstruct
    fun initialize() {
        try {
            logger.info("INITIALIZING FIREBASE")
            val key = FileInputStream(System.getenv("GOOGLE_SERVICE_KEY"))
            val storageBucket = System.getenv("FIREBASE_STORAGE_BUCKET")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(key))
                .setStorageBucket(storageBucket)
                .build()
            FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            logger.error("FAILED TO INITIALIZE FIREBASE", e)
        }
    }

}