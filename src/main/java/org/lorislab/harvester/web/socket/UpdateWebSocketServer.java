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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Andrej_Petras
 */
@ApplicationScoped
@ServerEndpoint("/update")
public class UpdateWebSocketServer {

    private static final Logger LOGGER = Logger.getLogger(UpdateWebSocketServer.class.getName());

    @Inject
    private WebSocketController controller;

    @OnOpen
    public void open(Session session) {
        LOGGER.log(Level.FINEST, "Add session {0}", session.getId());
        controller.add(session);
    }

    @OnClose
    public void close(Session session) {
        LOGGER.log(Level.FINEST, "Close session {0}", session.getId());
        controller.remove(session);
    }

    @OnMessage
    public void message(String message, Session session) {
        LOGGER.log(Level.INFO, "Message from client: {0}", message);
    }

}
