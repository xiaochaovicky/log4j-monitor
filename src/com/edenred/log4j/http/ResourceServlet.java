package com.edenred.log4j.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.edenred.log4j.util.Utils;

@SuppressWarnings("serial") 
public class ResourceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger
			.getLogger(ResourceServlet.class);

	protected final String resourcePath="support/http/resources";
	
	
	public ResourceServlet(){super();}
	
	protected String getFilePath(String fileName) {
		return resourcePath + fileName;
	}

	protected void returnResourceFile(String fileName, String uri,
			HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		String filePath = getFilePath(fileName);
		Utils utils = new Utils();
		if (fileName.endsWith(".jpg")) {
			
			byte[] bytes = utils.readByteArrayFromResource(filePath);
			if (bytes != null) {
				response.getOutputStream().write(bytes);
			}

			return;
		}

		String text = utils.readFromResource(filePath);
		if (text == null) {
			response.sendRedirect(uri + "/index.html");
			return;
		}
		if (fileName.endsWith(".css")) {
			response.setContentType("text/css;charset=utf-8");
		} else if (fileName.endsWith(".js")) {
			response.setContentType("text/javascript;charset=utf-8");
		} else if (fileName.endsWith("index.html")) {
			String localAdd = request.getLocalAddr();
			int port = request.getLocalPort();
			String contextPath = request.getContextPath();
			text = text.replace("ws://127.0.0.1:8080/SCMT/webSocket/echo", "ws://"+localAdd+":"+port+contextPath+"/webSocket/echo");
		}
		response.getWriter().write(text);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String requestURI = request.getRequestURI();

		if (contextPath == null) {
			contextPath = "";
		}

		String uri = contextPath + servletPath;
		String path = requestURI.substring(contextPath.length()
				+ servletPath.length());

		returnResourceFile(path, uri,request, response);
	}

}
