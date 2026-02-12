package Class.Service;

import Class.Dto.Response.SessionResponse;

public interface SessionService {
    SessionResponse startSession(String sessionId, String userId);
    SessionResponse completeSession(String sessionId, String userId);
}
