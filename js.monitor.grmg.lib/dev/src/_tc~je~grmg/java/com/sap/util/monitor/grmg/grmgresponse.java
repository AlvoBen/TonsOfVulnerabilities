/*
 *  last change 2003-11-10
 */

/**
 * Title:        GRMG Response
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 */

package com.sap.util.monitor.grmg;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*; 

/**
class wrapper for Grmg Response in form of XML document 
*/
public class GrmgResponse
{
	//private Document document;
	
	private GrmgScenario gs;
	private ByteArrayOutputStream output;
	private boolean isCompatibilityMode = false;
	
	private String lineEnd=System.getProperty("line.separator");
	/**
	returns GrmgScenario object associated with GrmgResponse object
	*/
	public GrmgScenario getScenario()
	{
		return gs;
	}
	
	/**
	Creates new GrmgResponse object
	*/
	public GrmgResponse() 
	{
		output=new ByteArrayOutputStream();
		gs=new GrmgScenario();	
	}
	
	/**
	Creates new GrmgResponse object from GrmgScenario object
	*/
	public GrmgResponse(GrmgScenario gs) 
	{
		output=new ByteArrayOutputStream();
		this.gs=gs;	
	}
	
	/**
	 returns ByteArrayOutputStream containing XML structure. 
	 For coding compatibility with the version 1.0
	 
	*/
	public ByteArrayOutputStream getOutput ()
	{
		if(!isCompatibilityMode) 	
		{
			return getOutputWithDom();
		}		
		
		else
		{
			
			return getOutputWithStringConcatenation();
		}
	}
	
	/**
	 Sets compatibility flag to true or false
	 for coding compatibility with the version 1.0
	 
	*/
	public void setCompatibilityMode(boolean isCompatible)
	{
		
		isCompatibilityMode = isCompatible;
				
	}
	
	/**
	 Returns compatibility flag
	 for coding compatibility with the version 1.0
	 
	*/
	public boolean getCompatibilityMode()
	{
		
		return isCompatibilityMode;
				
	}
	
	
	/**
	returns ByteArrayOutputStream containing XML structure
	*/
	private ByteArrayOutputStream getOutputWithDom/*getOutput2*/()
	{
			try
			{
				TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(getResponseAsDom());
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
	    	
		}
		catch (TransformerConfigurationException tce)
		{
			println(tce.getMessage());
		}
		catch (TransformerException te)
		{
			println(te.getMessage());
		}	
		catch(GrmgRequestException e)
		{
			println(e.getMessage());
		}			    	
		return output;
	}
	
	
	private ByteArrayOutputStream getOutputWithStringConcatenation() 
	{
		println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		println("<scenario>");
		println("<scenname>");
		
		println(gs.getName());
		
		println("</scenname>");
		println("<sceninst>");
		println(gs.getInstance());
		println("</sceninst>");
		
		println("<scenversion>");
		println(gs.getVersion());
		println("</scenversion>");
		
		if(gs.getNumberOfComponents()>0)
		{					
		 for(int i=0;i<gs.getNumberOfComponents();i++)
				{
					println("<component>");
					println("<compname>"+gs.getComponent(i).getName()+"</compname>");
					println("<compversion>"+gs.getComponent(i).getVersion()+"</compversion>");
					
          GrmgText description = gs.getComponent(i).getText();
					
          if (description != null && description.getDescription() != null && description.getDescription().length() > 0) { 
            println("<compdesc>"+description.getDescription()+"</compdesc>");
          }  
    			
					println("<comphost>"+gs.getComponent(i).getHost()+"</comphost>");
					println("<compinst>"+gs.getComponent(i).getInst()+"</compinst>");
    	    			    			
					println("<messages>");
					for (int j=0;j<gs.getComponent(i).getNumberOfMessages();j++)
					{
						println("<message>");
						String[] st=gs.getComponent(i).getMessage(j).getMessageParameters();
						if(st!=null)
						{
						println("<messalert>"+st[0]+"</messalert>");
						println("<messseverity>"+st[1]+"</messseverity>");
						println("<messarea>"+st[2]+"</messarea>");
    				
						println("<messnumber>"+st[3]+"</messnumber>");
						println("<messparam1>"+st[4]+"</messparam1>");
						println("<messparam2>"+st[5]+"</messparam2>");
						println("<messparam3>"+st[6]+"</messparam3>");
    				
						println("<messparam4>"+st[7]+"</messparam4>");
						println("<messtext>"+st[8]+"</messtext>");//st[8]
						}
					println("</message>");    				
					}
					println("</messages>");
					println("</component>");
				}			
		}
	println("</scenario>");
	return output;
 }
	
	private void println(String s)
	{
		output.write(s.getBytes(),0,s.length());
		
		output.write(lineEnd.getBytes(),0,lineEnd.length());  
	}

Document getResponseAsDom() throws GrmgRequestException
	{
		Document document;
		
		
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			try {
					DocumentBuilder builder =
					factory.newDocumentBuilder();
					document = builder.newDocument();  
					}
				catch (ParserConfigurationException pce) 
					{
					// Parser with specified options can't be built
					throw new GrmgRequestException("Parser Configuration Exception while creating Output");
				}	
		Element scenario = 
				(Element)
					document.createElement("scenario"); 
					document.appendChild(scenario);
  
				Element scenario_name = 
				(Element)
					document.createElement("scenname"); 
					scenario.appendChild(scenario_name);  
				scenario_name.appendChild(document.createTextNode(gs.getName()));

				Element scenario_instance = 
				(Element)
					document.createElement("sceninst"); 
					scenario.appendChild(scenario_instance);  
				scenario_instance.appendChild(document.createTextNode(gs.getInstance()));  
		

				Element scenario_version = 
				(Element)
					document.createElement("scenversion"); 
					scenario.appendChild(scenario_version);  
				scenario_version.appendChild(document.createTextNode(gs.getVersion()));    
        
				if(gs.getNumberOfComponents()>0)
				{
				for(int i=0;i<gs.getNumberOfComponents();i++)
				{          		
							Element component =  
							(Element)
							document.createElement("component");
							scenario.appendChild(component);
          		
							Element component_name =  
							(Element)
							document.createElement("compname");
							component.appendChild(component_name);
							component_name.appendChild(document.createTextNode(gs.getComponent(i).getName()));	
          		
							Element component_version =  
							(Element)
							document.createElement("compversion");
							component.appendChild(component_version);
							component_version.appendChild(document.createTextNode(gs.getComponent(i).getVersion()));
              
              GrmgText description = gs.getComponent(i).getText();
					
              if (description != null && description.getDescription() != null && description.getDescription().length() > 0) { 
								Element component_description =  
								(Element)
								document.createElement("compdesc");
								component.appendChild(component_description);
								component_description.appendChild(document.createTextNode(description.getDescription()));
              }
              
							Element component_host =  
							(Element)
							document.createElement("comphost");
							component.appendChild(component_host);
							component_host.appendChild(document.createTextNode(gs.getComponent(i).getHost()));
          		
							Element component_instance =  
							(Element)
							document.createElement("compinst");
							component.appendChild(component_instance);
							component_instance.appendChild(document.createTextNode(gs.getComponent(i).getInst()));
          		
							Element messages =  
							(Element)
							document.createElement("messages");
							component.appendChild(messages);
          		
							for (int j=0;j<gs.getComponent(i).getNumberOfMessages();j++)
					{
    			
					 Element message =  
							 (Element)
							 document.createElement("message");
							 messages.appendChild(message);
          		 
							 Element message_alert =  
							 (Element)
							 document.createElement("messalert");
							 message.appendChild(message_alert);
							 message_alert.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageAlert()));
          		
							 Element message_severity =  
							 (Element)
							 document.createElement("messseverity");
							 message.appendChild(message_severity);
							 message_severity.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageSeverity()));
          		 
							 Element message_area =  
							 (Element)
							 document.createElement("messarea");
							 message.appendChild(message_area);
							 message_area.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageArea()));
          		 
							 Element message_id =  
							 (Element)
							 document.createElement("messnumber");
							 message.appendChild(message_id);
							 message_id.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageId()));
          		 
							 Element message_parameter1 =  
							 (Element)
							 document.createElement("messparam1");
							 message.appendChild(message_parameter1);
							 message_parameter1.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageParameter1()));
          		 
							 Element message_parameter2 =  
							 (Element)
							 document.createElement("messparam2");
							 message.appendChild(message_parameter2);
							 message_parameter2.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageParameter2()));
          		
							 Element message_parameter3 =  
							 (Element)
							 document.createElement("messparam3");
							 message.appendChild(message_parameter3);
							 message_parameter3.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageParameter3()));
          		 
							 Element message_parameter4 =  
							 (Element)
							 document.createElement("messparam4");
							 message.appendChild(message_parameter4);
							 message_parameter4.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageParameter4()));
          		 
							 Element message_text =  
							 (Element)
							 document.createElement("messtext");
							 message.appendChild(message_text);
							 message_text.appendChild(document.createTextNode(gs.getComponent(i).getMessage(j).getMessageText()));
          		 
          		 
					}
          		
				}
		}            
		return document;
	}
}