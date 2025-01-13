package micro.trainersworkload.model;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WorkloadTest {

      @Autowired
      private EntityManager entityManager;

      @Test
      void testWorkloadEntity_ShouldPersistCorrectly() {
            Workload workload = new Workload();
            workload.setWorkloadYear(2025);
            workload.setWorkloadMonth(1);
            workload.setTotalDuration(120);
            workload.setTrainersUsername("testTrainer");

            entityManager.persist(workload);
            entityManager.flush();

            Workload retrieved = entityManager.find(Workload.class, workload.getId());

            assertNotNull(retrieved, "Workload entity should be persisted and retrievable");
            assertEquals(2025, retrieved.getWorkloadYear(), "Workload year should match");
            assertEquals(1, retrieved.getWorkloadMonth(), "Workload month should match");
            assertEquals(120, retrieved.getTotalDuration(), "Total duration should match");
            assertEquals("testTrainer", retrieved.getTrainersUsername(), "Trainer's username should match");
      }

      @Test
      void testWorkloadEntity_ShouldGenerateId() {
            Workload workload = new Workload();
            workload.setWorkloadYear(2025);
            workload.setWorkloadMonth(2);
            workload.setTotalDuration(60);
            workload.setTrainersUsername("testTrainer2");

            entityManager.persist(workload);
            entityManager.flush();

            assertNotNull(workload.getId(), "Workload ID should be generated after persistence");
      }

      @Test
      void testWorkloadEntity_ShouldHandleNoArgsConstructor() {
            Workload workload = new Workload();

            assertNotNull(workload, "Workload object should be created with no-args constructor");
      }

      @Test
      void testWorkloadEntity_ShouldHandleAllArgsConstructor() {
            Workload workload = new Workload(1L, 2025, 1, 120, "testTrainer");

            assertEquals(1L, workload.getId(), "ID should match");
            assertEquals(2025, workload.getWorkloadYear(), "Workload year should match");
            assertEquals(1, workload.getWorkloadMonth(), "Workload month should match");
            assertEquals(120, workload.getTotalDuration(), "Total duration should match");
            assertEquals("testTrainer", workload.getTrainersUsername(), "Trainer's username should match");
      }
}
