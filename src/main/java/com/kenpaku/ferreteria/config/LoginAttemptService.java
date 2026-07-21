package com.kenpaku.ferreteria.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptService {

    public static final String FAILED_LOGIN_COUNT = "FAILED_LOGIN_COUNT";
    public static final String LOGIN_LOCKED = "LOGIN_LOCKED";
    public static final int MAX_ATTEMPTS = 3;

    public int incrementFailedAttempt(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Integer attempts = (Integer) session.getAttribute(FAILED_LOGIN_COUNT);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        session.setAttribute(FAILED_LOGIN_COUNT, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            session.setAttribute(LOGIN_LOCKED, Boolean.TRUE);
        }
        return attempts;
    }

    public int getFailedAttempts(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        Integer attempts = (Integer) session.getAttribute(FAILED_LOGIN_COUNT);
        return attempts == null ? 0 : attempts;
    }

    public boolean isLocked(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return Boolean.TRUE.equals(session.getAttribute(LOGIN_LOCKED));
    }

    public void reset(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(FAILED_LOGIN_COUNT);
            session.removeAttribute(LOGIN_LOCKED);
        }
    }
}
