package com.sap.portletbrowser.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sap.portletbrowser.template.TemplateBuilder;

public class ContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void contextInitialized(ServletContextEvent event) {
		// init template engine and put a reference in the application session
		TemplateBuilder builder = new TemplateBuilder(event.getServletContext());
		event.getServletContext().setAttribute("template_builder", builder);

	}

}
