package nl.tudelft.sem.template.authentication.services;

import java.util.ArrayList;
import nl.tudelft.sem.template.authentication.entities.AppUser;
import nl.tudelft.sem.template.authentication.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The UserService.
 */
@Service
public class UserService implements UserDetailsService {
    private final transient UserRepository userRepository;
    private final transient PasswordEncoder passwordEncoder;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository  the user repository
     * @param passwordEncoder the password encoder
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String netid) throws UsernameNotFoundException {
        var optionalUser = userRepository.findById(netid);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User does not exist");
        }

        var user = optionalUser.get();

        return new User(user.getNetid(), user.getPasswordHash(),
                new ArrayList<>()); // no authorities
    }


    /**
     * Create a user.
     *
     * @param netid    The netid of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public void createUser(String netid, String password) throws Exception {
        if (userRepository.existsById(netid)) {
            throw new Exception("User already exists");
        }

        String passwordHash = passwordEncoder.encode(password);

        AppUser user = new AppUser();
        user.setNetid(netid);
        user.setPasswordHash(passwordHash);

        userRepository.save(user);
    }
}
