package com.sap.portletbrowser.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.servlet.ServletContext;

public class TemplateBuilder {
	ServletContext ctx;
	HashMap<String, StringBuilder> templates;
	
	
	public TemplateBuilder(ServletContext context){
		this.ctx = context;
		templates = new HashMap<String, StringBuilder>();
	}
	
	
	
	public StringBuilder getTemplate(String key){
		if (templates.containsKey(key)){
			return templates.get(key);
		}
		
		StringBuilder template = createTemplate(key);
		templates.put(key, template);
		return template;
		
	}
	
	
	private StringBuilder createTemplate(String key) {
		// it should be done only once
		  InputStream is = ctx.getResourceAsStream(key);
		  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		  StringBuilder portlet_template = new StringBuilder(1024);
		  String line="";
		  try {
			while ((line = reader.readLine()) != null){
				portlet_template.append(line);  
			  }
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  return portlet_template;
	}
	
}
