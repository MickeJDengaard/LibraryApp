package dk.kea.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.kea.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
