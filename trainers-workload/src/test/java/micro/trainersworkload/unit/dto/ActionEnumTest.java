package micro.trainersworkload.unit.dto;

import micro.trainersworkload.dto.ActionEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionEnumTest {

  @Test
  void testEnumValues() {

    ActionEnum[] values = ActionEnum.values();
    assertEquals(2, values.length);
    assertEquals(ActionEnum.ADD, values[0]);
    assertEquals(ActionEnum.DELETE, values[1]);
  }

  @Test
  void testValueOf() {
    assertEquals(ActionEnum.ADD, ActionEnum.valueOf("ADD"));
    assertEquals(ActionEnum.DELETE, ActionEnum.valueOf("DELETE"));

    assertThrows(IllegalArgumentException.class, () -> ActionEnum.valueOf("UPDATE"));
  }
}
