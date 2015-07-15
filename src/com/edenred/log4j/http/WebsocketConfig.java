package com.edenred.log4j.http;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

public class WebsocketConfig implements ServerApplicationConfig {
	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(
			Set<Class<? extends Endpoint>> scanned) {
		System.out.println("******getEndpointConfigs******");

		Set<ServerEndpointConfig> result = new HashSet<ServerEndpointConfig>();

		if (scanned.contains(WebSocketServlet.class)) {
			result.add(ServerEndpointConfig.Builder.create(
					WebSocketServlet.class, "/webSocket/echo").build());
		}

		return result;
	}

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		// Deploy all WebSocket endpoints defined by annotations in the examples
		// web application. Filter out all others to avoid issues when running
		// tests on Gump
		System.out.println("******getAnnotatedEndpointClasses******");
		Set<Class<?>> results = new HashSet<Class<?>>();
		for (Class<?> clazz : scanned) {
			if (clazz.getPackage().getName().startsWith("websocket.")) {
				results.add(clazz);
			}
		}
		return results;
	}
}
