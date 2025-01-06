package micro.trainersworkload.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainerTest {

    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("John.Doe");
        trainer.setStatus(true);
    }

    @Test
    public void testTrainerCreation() {
        assertNotNull(trainer);
        assertEquals("John", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertEquals("John.Doe", trainer.getUsername());
        assertTrue(trainer.isStatus());
    }

    @Test
    public void testTrainerWorkloads() {
        Set<Workload> workloads = new HashSet<>();
        trainer.setWorkloads(workloads);

        assertNotNull(trainer.getWorkloads());
        assertEquals(0, trainer.getWorkloads().size());

        Workload workload = new Workload();
        workload.setTrainer(trainer);
        workloads.add(workload);

        assertEquals(1, trainer.getWorkloads().size());
    }

    @Test
    public void testTotalDuration() {
        trainer.setTotalDuration(100);
        assertEquals(100, trainer.getTotalDuration());
    }
}
