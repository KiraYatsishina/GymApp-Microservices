package micro.gymapp.mapper;

import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getPassword()
        );
    }
}
