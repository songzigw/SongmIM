package songm.im.mqtt;

import songm.im.entity.SessionCh;

public interface ClientUser {

    public void addSession(SessionCh session);

    public void removeSession(SessionCh session);

    public boolean isSessions();

    public void clearSessions();
}
