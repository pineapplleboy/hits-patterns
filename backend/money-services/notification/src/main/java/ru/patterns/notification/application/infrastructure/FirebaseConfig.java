package ru.patterns.notification.application.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebaseApp() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp успешно инициализирован");
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Не удалось инициализировать FirebaseApp. " +
                            "Проверьте переменную GOOGLE_APPLICATION_CREDENTIALS и путь к service account JSON.",
                    exception
            );
        }
    }
}
