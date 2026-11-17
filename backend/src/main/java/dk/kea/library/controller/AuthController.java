package dk.kea.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dk.kea.library.dto.CredentialsRequest;
import dk.kea.library.dto.MeResponse;
import dk.kea.library.dto.UserResponse;
import dk.kea.library.model.User;
import dk.kea.library.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@RequestBody CredentialsRequest body) {
        User user = userService.signup(body.username(), body.password());
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody CredentialsRequest body, HttpServletRequest request) {
        User user = userService.login(body.username(), body.password());
        HttpSession session = request.getSession(true);
        request.changeSessionId(); // new session id after login
        session.setAttribute(SessionUser.USER_ID, user.getId());
        return UserResponse.from(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SessionUser.requireUserId(session);
        session.invalidate();
    }

    @GetMapping("/me")
    public MeResponse me(HttpServletRequest request) {
        long userId = SessionUser.requireUserId(request.getSession(false));
        User user = userService.getUser(userId);
        return new MeResponse(user.getId(), user.getUsername(), 0); // owedKr comes with feature/loans
    }
}
