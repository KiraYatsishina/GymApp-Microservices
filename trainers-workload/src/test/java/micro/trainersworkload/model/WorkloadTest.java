package micro.trainersworkload.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadTest {

  @Test
  void testWorkloadCreationAndGetters() {
    List<Year> years = new ArrayList<>();
    Workload workload = new Workload(
        "1",
        "trainer1",
        "John",
        "Doe",
        true,
        years
    );

    assertEquals("1", workload.getId());
    assertEquals("trainer1", workload.getUsername());
    assertEquals("John", workload.getFirstName());
    assertEquals("Doe", workload.getLastName());
    assertTrue(workload.isStatus());
    assertEquals(years, workload.getYears());
  }

  @Test
  void testWorkloadSetters() {
    Workload workload = new Workload();
    workload.setId("1");
    workload.setUsername("trainer1");
    workload.setFirstName("John");
    workload.setLastName("Doe");
    workload.setStatus(true);
    workload.setYears(new ArrayList<>());

    assertEquals("1", workload.getId());
    assertEquals("trainer1", workload.getUsername());
    assertEquals("John", workload.getFirstName());
    assertEquals("Doe", workload.getLastName());
    assertTrue(workload.isStatus());
    assertNotNull(workload.getYears());
  }

  @Test
  void testWorkloadEqualsAndHashCode() {
    Workload workload1 = new Workload(
        "1",
        "trainer1",
        "John",
        "Doe",
        true,
        new ArrayList<>()
    );

    Workload workload2 = new Workload(
        "1",
        "trainer1",
        "John",
        "Doe",
        true,
        new ArrayList<>()
    );

    assertEquals(workload1, workload2);
    assertEquals(workload1.hashCode(), workload2.hashCode());
  }

  @Test
  void testWorkloadToString() {
    Workload workload = new Workload(
        "1",
        "trainer1",
        "John",
        "Doe",
        true,
        new ArrayList<>()
    );

    String expected = "Workload(id=1, username=trainer1, firstName=John, lastName=Doe, status=true, years=[])";
    assertEquals(expected, workload.toString());
  }
}

