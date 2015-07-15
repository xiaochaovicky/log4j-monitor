package com.edenred.log4j.appender;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.edenred.log4j.http.WebSocketServlet;
import com.edenred.log4j.util.EhcacheUtil;

public class WebConsoleAppender extends ConsoleAppender {

	protected Set<WebSocketServlet> clients = new CopyOnWriteArraySet<WebSocketServlet>();


	public Set<WebSocketServlet> getClients() {
		return clients;
	}

	public void setClients(Set<WebSocketServlet> clients) {
		this.clients = clients;
	}
	
	@Override
	protected void subAppend(LoggingEvent event) {
		
		EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
		Set<com.edenred.log4j.http.WebSocketServlet> clients = (Set<com.edenred.log4j.http.WebSocketServlet>) ehcacheUtil.get("log4jMontior", "clientList");
		if(clients!=null&&clients.size()>0){
			for (WebSocketServlet client : clients) {
				try {
					client.getSession().getBasicRemote().sendText(this.layout.format(event));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					clients.remove(client);
					e.printStackTrace();
				}
			}
		}
		//super.subAppend(event);
	}
}
