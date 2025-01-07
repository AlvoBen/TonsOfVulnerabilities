package com.sap.util.monitor.grmg;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

import java.io.*;
// import java.util.*;

/**
 * Title:        GRMG Request
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 * @author Georgi Mihailov i026851
 * @version 640
 */

/**
	class wrapper for GRMG request in form of XML
*/
public class GrmgRequest
{
	private Document document;
	private GrmgScenario gs;
	
	/**
		Returns GrmgScenario object
		*/
	public GrmgScenario getScenario()
	{
		

		return gs;
	}
	
	GrmgRequest()
	{		
		gs= new GrmgScenario();
	}
	
	/**
		Creates GrmgRequest object from InputStream          
	*/
	public GrmgRequest(InputStream is) throws GrmgRequestException{
		
		gs= new GrmgScenario();
       
		try{
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     factory.setIgnoringElementContentWhitespace(true);
     DocumentBuilder builder = factory.newDocumentBuilder();
     document = builder.parse( is );
     prepare();
    }
    catch(FactoryConfigurationError e){
     e.printStackTrace();
     throw new GrmgRequestException("Document Builder Factory Configuration Error");
    }
    catch (ParserConfigurationException e){
     e.printStackTrace();
     throw new GrmgRequestException("Parser Configuration Exception");
    }
    catch(org.xml.sax.SAXException e){
    	e.printStackTrace();
		 throw new GrmgRequestException("SAX Parser Exception");    		
    }
    catch(IOException e){
     e.printStackTrace();	
     throw new GrmgRequestException("Input Stream Error");
    }
	}

	/**
	Creates GrmgRequest object from File with specified filename 
	*/
	public GrmgRequest(String filename) throws GrmgRequestException{
		
		gs= new GrmgScenario();
		
		try{
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     factory.setIgnoringElementContentWhitespace(true);	
     DocumentBuilder builder = factory.newDocumentBuilder();
     document = builder.parse( new File(filename) );
     prepare();
    }
		catch(FactoryConfigurationError e){
		 e.printStackTrace();
		 throw new GrmgRequestException("Document Builder Factory Configuration Error");
		}
		catch (ParserConfigurationException e){
		 e.printStackTrace();
		 throw new GrmgRequestException("Parser Configuration Exception");
		}
		catch(org.xml.sax.SAXException e){
			e.printStackTrace();
		 throw new GrmgRequestException("SAX Parser Exception");    		
		}
		catch(IOException e){
		 e.printStackTrace();	
		 throw new GrmgRequestException("Input Stream Error");
		}
	}
		
	void prepare(){
		parseDOM(document/*.getDocumentElement(),gs*/);
	}
	
	int parseDOM(Node nl/*,GrmgScenario gs*/){
		
		int type = nl.getNodeType();

    switch (type){
     case Node.ATTRIBUTE_NODE:
     case Node.CDATA_SECTION_NODE:
     case Node.COMMENT_NODE:
     case Node.DOCUMENT_FRAGMENT_NODE:
     case Node.DOCUMENT_NODE:
     case Node.DOCUMENT_TYPE_NODE:
     case Node.ENTITY_NODE:
     case Node.ENTITY_REFERENCE_NODE:
     case Node.NOTATION_NODE:
     case Node.PROCESSING_INSTRUCTION_NODE:
     case Node.ELEMENT_NODE:
     // System.out.println(nl.getNodeName());
        
        if(nl.getNodeName().toLowerCase().trim().equals("property"))
         	{
         		gs.getCurrentComponent().addProperty();
         		break;
         	
         	} 	
        if(nl.getNodeName().toLowerCase().trim().equals("component"))
         	{
         		gs.addComponent();
         		break;
         	}        
        
        break;
        case Node.TEXT_NODE:
        	
        	
         try{
         	if(!nl.getNodeValue().trim().equals(""))
         	{
         	if(nl.getParentNode().getNodeName().toLowerCase().equals("scenversion"))
         	{
         		gs.setVersion(nl.getNodeValue());   	
         	break;
         	}
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("scenname"))
         	{
         		gs.setName(nl.getNodeValue());
         		break;
         	}
         	
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("sceninst"))
         	{
         		
         		gs.setInstance(nl.getNodeValue());
         		break;
         	}
          
          //{{ i026851
          if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("scenclientsid"))
          {
            
            gs.setClientSID(nl.getNodeValue());
            break;
          }
          
          if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("scenclientsrv"))
          {
            
            gs.setClientServer(nl.getNodeValue());
            break;
          }
          //}} i026851
                   	
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("compname"))
         	{
         		
         		gs.getCurrentComponent().setName(nl.getNodeValue());
         		break;
         	}
         	
         	
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("compversion"))
         	{
         		
         		gs.getCurrentComponent().setVersion(nl.getNodeValue());
          		break;
         	}
         	
					if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("comptype"))
					{
         		
						gs.getCurrentComponent().setType(nl.getNodeValue());
							break;
					}
         	
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("propname"))
         	{         	
         		gs.getCurrentComponent().getCurrentProperty().setName(nl.getNodeValue());
         		break;
         	}
         	
         	
         	if(nl.getParentNode().getNodeName().toLowerCase().trim().equals("propvalue"))
         	{
         		
         		gs.getCurrentComponent().getCurrentProperty().setValue(nl.getNodeValue());
         		break;
         	}         	     
        }
       		 
        	}
        	catch(Exception e){e.printStackTrace();}   
        	
            break;
        default:
        
            break;
        }
        if(nl.getFirstChild()!=null)
        {
      
        for (Node child = nl.getFirstChild(); child != null;
             child = child.getNextSibling()) {
            int j=parseDOM(child/*,gs*/);
            if(j== -1) return -1;
        }
    	}
        
		return 1;
		
	}
		
	void printLS()
	{	}
}