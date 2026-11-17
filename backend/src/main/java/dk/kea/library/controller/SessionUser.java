package dk.kea.library.controller;

import jakarta.servlet.http.HttpSession;

import dk.kea.library.exception.NotLoggedInException;

/** Reads the logged-in user's id from the session. */
public final class SessionUser {

    public static final String USER_ID = "userId";

    private SessionUser() {
    }

    /** Returns the logged-in user's id, or throws 401 if nobody is logged in. */
    public static long requireUserId(HttpSession session) {
        if (session == null) {
            throw new NotLoggedInException();
        }
        Object userId = session.getAttribute(USER_ID);
        if (userId == null) {
            throw new NotLoggedInException();
        }
        return (Long) userId;
    }
}
