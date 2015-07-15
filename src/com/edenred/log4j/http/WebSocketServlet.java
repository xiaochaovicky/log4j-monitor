package com.edenred.log4j.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.apache.log4j.PropertyConfigurator;

import com.edenred.log4j.util.EhcacheUtil;


public class WebSocketServlet extends Endpoint {

	private static final AtomicInteger connectionIds = new AtomicInteger(0);

	private static final Set<WebSocketServlet> connections = new CopyOnWriteArraySet<WebSocketServlet>();
	private static final String GUEST_PREFIX = "Guest";

	private Session session;

	private final String nickname;

	public WebSocketServlet() {
		nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		RemoteEndpoint.Basic remoteEndpointBasic = session.getBasicRemote();
		session.addMessageHandler(new EchoMessageHandler(remoteEndpointBasic));
		this.session = session;
		connections.add(this);
		sendAll();

	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		connections.remove(this);
		sendAll();
		// super.onClose(session, closeReason);
	}

	public static void sendAll() {
		EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
		ehcacheUtil.put("log4jMontior", "clientList", connections);
	}

	private static class EchoMessageHandler implements
			MessageHandler.Whole<String> {

		private final RemoteEndpoint.Basic remoteEndpointBasic;

		private EchoMessageHandler(RemoteEndpoint.Basic remoteEndpointBasic) {
			this.remoteEndpointBasic = remoteEndpointBasic;
		}

		@Override
		public void onMessage(String message) {
			try {
				if (remoteEndpointBasic != null) {
					remoteEndpointBasic.sendText("Send message:" + message);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
