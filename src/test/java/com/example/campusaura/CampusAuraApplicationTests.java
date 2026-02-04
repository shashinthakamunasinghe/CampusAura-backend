package com.example.campusaura;

import com.example.campusaura.config.TestFirestoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestFirestoreConfig.class)
class CampusAuraApplicationTests {

    @Test
    void contextLoads() {
        // Test that Spring context loads successfully
    }

}
