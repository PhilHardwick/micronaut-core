/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.server.netty.websocket;

//tag::clazz[]
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import java.util.function.Predicate;

@ServerWebSocket("/chat/{topic}/{username}") // <1>
public class ChatServerWebSocket {
    private WebSocketBroadcaster broadcaster;
    private String subProtocol;

    public ChatServerWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen // <2>
    public void onOpen(String topic, String username, WebSocketSession session) {
        this.subProtocol = session.getSubprotocol().orElse(null);
        String msg = "[" + username + "] Joined!";
        assert ServerRequestContext.currentRequest().isPresent();
        broadcaster.broadcastSync(msg, isValid(topic, session));
    }

    @OnMessage // <3>
    public void onMessage(
            String topic,
            String username,
            String message,
            WebSocketSession session) {
        String msg = "[" + username + "] " + message;
        assert ServerRequestContext.currentRequest().isPresent();
        broadcaster.broadcastSync(msg, isValid(topic, session)); // <4>
    }

    @OnClose // <5>
    public void onClose(
            String topic,
            String username,
            WebSocketSession session) {
        String msg = "[" + username + "] Disconnected!";
        assert ServerRequestContext.currentRequest().isPresent();
        broadcaster.broadcastSync(msg, isValid(topic, session));
    }

    private Predicate<WebSocketSession> isValid(String topic, WebSocketSession session) {
        return s -> s != session && topic.equalsIgnoreCase(s.getUriVariables().get("topic", String.class, null));
    }

    public String getSubProtocol() {
        return subProtocol;
    }
}
//end::clazz[]
