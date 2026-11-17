package dk.kea.library.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dk.kea.library.exception.InvalidPasswordException;
import dk.kea.library.exception.InvalidUsernameException;
import dk.kea.library.exception.NotFoundException;
import dk.kea.library.exception.UsernameTakenException;
import dk.kea.library.exception.WrongCredentialsException;
import dk.kea.library.model.User;
import dk.kea.library.repository.UserRepository;

@Service
public class UserService {

    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 64;

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_]+$";

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder, Clock clock) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public User signup(String username, String password) {
        if (!isValidUsername(username)) {
            throw new InvalidUsernameException(); // BR-01
        }
        if (!isValidPassword(password)) {
            throw new InvalidPasswordException(); // BR-02
        }
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new UsernameTakenException(); // BR-01 (unique, ignoring case)
        }
        String hash = passwordEncoder.encode(password);
        User user = new User(username, hash, LocalDateTime.now(clock));
        return users.save(user);
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            throw new WrongCredentialsException();
        }
        User user = users.findByUsernameIgnoreCase(username)
                .orElseThrow(WrongCredentialsException::new);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new WrongCredentialsException();
        }
        return user;
    }

    public User getUser(long id) {
        return users.findById(id).orElseThrow(NotFoundException::new);
    }

    /** BR-01: 3-20 characters, only letters, digits and _ */
    public boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        int length = username.length();
        if (length < USERNAME_MIN_LENGTH || length > USERNAME_MAX_LENGTH) {
            return false;
        }
        return username.matches(USERNAME_PATTERN);
    }

    /** BR-02: 8-64 characters */
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        int length = password.length();
        return length >= PASSWORD_MIN_LENGTH && length <= PASSWORD_MAX_LENGTH;
    }
}
