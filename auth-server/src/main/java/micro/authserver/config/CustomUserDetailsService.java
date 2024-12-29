package micro.authserver.config;

import lombok.RequiredArgsConstructor;
import micro.authserver.entity.User;
import micro.authserver.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credentials = repository.findByUsername(username);
        return credentials.orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
