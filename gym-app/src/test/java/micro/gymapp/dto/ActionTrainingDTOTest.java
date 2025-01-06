package micro.gymapp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTrainingDTOTest {

    @Test
    void testBuilderAndGetters() {
        ActionTrainingDTO dto = ActionTrainingDTO.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2025-01-01")
                .duration(60)
                .actionType("CREATE")
                .build();

        assertEquals("john_doe", dto.getUserName());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertTrue(dto.isActive());
        assertEquals("2025-01-01", dto.getTrainingDate());
        assertEquals(60, dto.getDuration());
        assertEquals("CREATE", dto.getActionType());
    }

    @Test
    void testSetters() {
        ActionTrainingDTO dto = new ActionTrainingDTO();
        dto.setUserName("john_doe");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setActive(true);
        dto.setTrainingDate("2025-01-01");
        dto.setDuration(60);
        dto.setActionType("CREATE");

        assertEquals("john_doe", dto.getUserName());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertTrue(dto.isActive());
        assertEquals("2025-01-01", dto.getTrainingDate());
        assertEquals(60, dto.getDuration());
        assertEquals("CREATE", dto.getActionType());
    }

    @Test
    void testEqualsAndHashCode() {
        ActionTrainingDTO dto1 = ActionTrainingDTO.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2025-01-01")
                .duration(60)
                .actionType("CREATE")
                .build();

        ActionTrainingDTO dto2 = ActionTrainingDTO.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2025-01-01")
                .duration(60)
                .actionType("CREATE")
                .build();

        ActionTrainingDTO dto3 = ActionTrainingDTO.builder()
                .userName("jane_doe")
                .firstName("Jane")
                .lastName("Doe")
                .isActive(false)
                .trainingDate("2025-01-02")
                .duration(45)
                .actionType("UPDATE")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        ActionTrainingDTO dto = ActionTrainingDTO.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2025-01-01")
                .duration(60)
                .actionType("CREATE")
                .build();

        String expected = "ActionTrainingDTO(userName=john_doe, firstName=John, lastName=Doe, isActive=true, trainingDate=2025-01-01, duration=60, actionType=CREATE)";
        assertEquals(expected, dto.toString());
    }
}
