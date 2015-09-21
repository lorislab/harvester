/*
 * Copyright 2015 Andrej_Petras.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.harvester.web.socket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.websocket.Session;
import org.lorislab.harvester.web.events.UpdateViewEvent;

/**
 *
 * @author Andrej_Petras
 */
@ApplicationScoped
public class WebSocketController {

    private static final Logger LOGGER = Logger.getLogger(WebSocketController.class.getName());

    private final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public void add(Session session) {
        LOGGER.log(Level.INFO, "Add session {0}", session.getId());
        sessions.add(session);
    }

    public void remove(Session session) {
        LOGGER.log(Level.INFO, "Close session {0}", session.getId());
        sessions.remove(session);
    }

    public void sendMessage(@Observes @UpdateViewEvent String message) {
        sessions.forEach((item) -> sendMessage(item, message));
    }

    private void sendMessage(Session session, String message) {
        try {
            LOGGER.log(Level.FINEST, " {0} send update event {1}", new Object[]{session.getId(), message});
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(UpdateWebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
