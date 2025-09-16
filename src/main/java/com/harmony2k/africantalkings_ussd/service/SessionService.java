package com.harmony2k.africantalkings_ussd.service;

import com.harmony2k.africantalkings_ussd.model.Session;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private static final long SESSION_TIMEOUT_MS = 90_000L; // 90 seconds
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session getSession(String sessionId, String phoneNumber) {
        cleanupExpiredSessions();

        return sessions.computeIfAbsent(sessionId, id -> {
            System.out.println("Creating new session for: " + phoneNumber);
            return new Session(sessionId, phoneNumber);
        });
    }

    public void updateSession(Session session) {
        session.touch();
        sessions.put(session.getSessionId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    private void cleanupExpiredSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired(SESSION_TIMEOUT_MS));
    }

    public int getActiveSessionsCount() {
        return sessions.size();
    }
}