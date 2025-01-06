package micro.gymapp.mapperTest;

import micro.gymapp.dto.UserDTO;
import micro.gymapp.mapper.UserMapper;
import micro.gymapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("test.user");
        user.setPassword("securePassword123");
    }

    @Test
    void testToDTO() {
        UserDTO userDTO = UserMapper.toDTO(user);

        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getPassword(), userDTO.getPassword());
    }
}