package dk.kea.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dk.kea.library.dto.CredentialsRequest;
import dk.kea.library.dto.MeResponse;
import dk.kea.library.dto.UserResponse;
import dk.kea.library.model.User;
import dk.kea.library.service.LoanService;
import dk.kea.library.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LoanService loanService;

    public AuthController(UserService userService, LoanService loanService) {
        this.userService = userService;
        this.loanService = loanService;
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

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/me")
    public MeResponse me(HttpServletRequest request) {
        long userId = SessionUser.requireUserId(request.getSession(false));
        User user = userService.getUser(userId);
        return new MeResponse(user.getId(), user.getUsername(), loanService.owedKr(userId));
    }
}
