package micro.authserver.service;

import lombok.RequiredArgsConstructor;
import micro.authserver.entity.User;
import micro.authserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

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
}

