package com.example.audit;

import com.example.audit.domain.AuditService;
import com.google.api.core.ApiFuture;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@Testcontainers
//@ActiveProfiles("test")
public class AuditApplicationFirestoreContainerTests {
  @BeforeEach
  public void setup() {
    FirestoreOptions options = FirestoreOptions.getDefaultInstance().toBuilder()
        .setHost(firestoreEmulator.getEmulatorEndpoint())
        .setCredentials(NoCredentials.getInstance())
        .setProjectId("fake-test-project-id")
        .build();
    Firestore firestore = options.getService();

    this.eventService = new AuditService(options, firestore);
  }

  @Container
  private static final FirestoreEmulatorContainer firestoreEmulator =
      new FirestoreEmulatorContainer(
          DockerImageName.parse(
              "gcr.io/google.com/cloudsdktool/cloud-sdk:438.0.0-emulators"));

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.firestore.host-port", firestoreEmulator::getEmulatorEndpoint);
  }

  // @Autowired
  private AuditService eventService;

//  @Disabled("Until Spring Boot 3.1 is released")
  @Test
  void testEventRepositoryStoreImage() throws ExecutionException, InterruptedException {
    ApiFuture<WriteResult> writeResult = eventService.auditQuote("test quote", "test author", "test book", UUID.randomUUID().toString());
    Assertions.assertNotNull(writeResult.get().getUpdateTime());
  }
}