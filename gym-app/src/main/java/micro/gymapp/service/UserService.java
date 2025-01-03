package micro.gymapp.service;

import micro.gymapp.dto.UserDTO;
import micro.gymapp.mapper.UserMapper;
import micro.gymapp.model.Role;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Not found '%s'", username)));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Transactional
    public boolean deleteUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.deleteByUsername(username);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean changeStatusByUsername(String username, boolean status) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setActive(status);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedNewPassword);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public  <T extends User> Optional<UserDTO> signUpUser(T user, Role role, Function<T, T> saveFunction) {
        long count = userRepository.countByFirstNameAndLastName(user.getFirstName(), user.getLastName());
        String username = generateUniqueUsername(user.getFirstName(), user.getLastName(), count);
        String generatedPassword = generatePassword();
        String encodedPassword = passwordEncoder.encode(generatedPassword);

        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setActive(true);

        T savedUser = saveFunction.apply(user);

        UserDTO userDTO = UserMapper.toDTO(savedUser);
        userDTO.setPassword(generatedPassword);

        return Optional.of(userDTO);
    }

    private String generateUniqueUsername(String firstName, String lastName, long count) {
        return firstName + "." + lastName + (count > 0 ? count : "");
    }

    private String generatePassword() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            passwordBuilder.append(characters.charAt(index));
        }
        return passwordBuilder.toString();
    }
}
