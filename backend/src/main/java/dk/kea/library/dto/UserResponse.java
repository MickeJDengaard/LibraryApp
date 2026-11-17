package dk.kea.library.dto;

import dk.kea.library.model.User;

public record UserResponse(Long id, String username) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
