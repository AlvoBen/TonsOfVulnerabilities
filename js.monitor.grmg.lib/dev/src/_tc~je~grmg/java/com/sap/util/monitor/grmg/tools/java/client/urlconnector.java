/*
 *  last change 2004-03-05
 */
 
 /**
 * @author Bernhard Drabant
 *
 * 
 */
package com.sap.util.monitor.grmg.tools.java.client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;

//import javax.xml.parsers.DocumentBuilder; 
//import javax.xml.parsers.DocumentBuilderFactory;  
//import javax.xml.parsers.ParserConfigurationException;

//import javax.xml.transform.dom.*;
//import javax.xml.transform.*;
//import javax.xml.transform.stream.*; 

//import org.xml.sax.SAXException;

//import java.awt.*;
//import javax.swing.*;

import com.sap.util.monitor.grmg.tools.runtime.RuntimeArguments;
import com.sap.util.monitor.grmg.tools.xml.*;

//import com.sap.util.monitor.grmg.*;
//import java.security.KeyStore;
//import javax.net.ssl.*;
//import java.security.cert.*;

public class URLConnector {

	static Properties props = new Properties();
	//ProgressMonitor pMonitor;

//	public static void main(String[] args) {
//
//		int j = 0;
//		// String msg = "x";
//		//Object obj = new Object();
//		boolean getFlag = false;
//		URL ur;
//
//		String server;
//		String servlet;
//		String servletName;
//		String iViewName;
//		String port;
//		String type;
//		String xmlfile;
//		String user="x";
//		String password="x";
//		String protocol = "http";
//		String fullurl ="";
//		//String storePassword="";
//		//String trustStore="";
//		//KeyStore keystore = null;
//		
//		int count = 0;
// 
//    RuntimeArguments rta = new RuntimeArguments(args);
//
//		try {      
//       if((xmlfile = rta.getProperty("file")) == null)
//        // xmlfile = "C:\\SAPMarkets\\Entwicklung\\Heartbeats-GRMG\\grmgRequest.xml";
//        xmlfile = "grmgRequest.xml";
//
//			if((server = rta.getProperty("server")) == null)
//			 server = "10.20.18.95";
//
//			if((fullurl = rta.getProperty("fullurl")) == null)
//			 fullurl = "http://localhost";
//
//       // not supported in this way in JDK 1.3.x
//			 if(rta.getProperty("ssl") != null)
//			  protocol = "https";
//
//       if((port = rta.getProperty("port")) == null)
//        port = "8090";
//
//			if((servletName = rta.getProperty("servlet")) == null)
//			 servletName = "ScenarioServlet";
//
//      /*
//			if((trustStore = rta.getProperty("truststore")) == null)
//			 trustStore = "";
//
//			if((storePassword = rta.getProperty("truststorepwd")) == null)
//			 storePassword = "";
//      */
//       
//       if((iViewName = rta.getProperty("iview")) == null)
//        iViewName = "TestParProject";
//         
//			if(rta.containsPureArgument("get"))
//			 getFlag = true;
//
//       if((type = rta.getProperty("type")) == null){
//
//        if((user = rta.getProperty("user")) != null && (password = rta.getProperty("password")) != null)            
//         servlet = "irj/servlet/prt/portal/prtroot/" + iViewName + "?j_user=" + user +
//                   "&j_password=" + password + "&login_submit=off";
//                   // &intclustertest=ok                
//        else
//         servlet = "heartbeat/servlet/" + servletName;        
//       }         
//       else
//        servlet = "irj/servlet/prt/portal/prtroot/" + iViewName + "." + type;
//
//			String userPassword = user + ":" + password;
//			String encodedUser = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
//
//   		if(!getFlag && fullurl.equals("http://localhost"))
//   		 ur = new URL(protocol + "://" + server + ":" + port + "/" + servlet);
//   		else
//   		 ur = new URL(fullurl); 
// 	 	
//			URLConnection hurlconn = ur.openConnection();
//
//			if (hurlconn instanceof HttpURLConnection) {
//
//				((HttpURLConnection) hurlconn).setDoOutput(true);
//				((HttpURLConnection) hurlconn).setDoInput(true);
//  			((HttpURLConnection) hurlconn).setRequestProperty("Content-Type", "text/html");
//				//((HttpURLConnection) hurlconn).setRequestProperty("Accept-Encoding", "gzip");
//				//((HttpURLConnection) hurlconn).setRequestProperty("Accept-Encoding", "deflate");
//				((HttpURLConnection) hurlconn).setRequestProperty("Authorization", "Basic " + encodedUser);
//
//				System.out.print("Start getting connection (" + (getFlag?"GET ":"POST ") + "request)\n");
//
//        if(getFlag)
//   			 ((HttpURLConnection) hurlconn).setRequestMethod("GET");        				
//				else{									 
//				 ((HttpURLConnection) hurlconn).setRequestMethod("POST");
//				  
//				 File file = new File(xmlfile);
//				 System.out.println("to " + ur);
//				 System.out.println("\nRequest containing file " + file + " :\n");
//
//				 FileInputStream fis = new FileInputStream(file.getAbsolutePath());
//					while (fis.read() != -1) {
//						j += 1;
//					}
//				 fis.close();
//
//				 fis = new FileInputStream(file.getAbsolutePath());
//				 byte[] barray = new byte[j];
//				 fis.read(barray);
//				 fis.close();
//         
//				 OutputStream out = hurlconn.getOutputStream();
//
//				 for (int i = 0; i < barray.length; i += 1) {
//					System.out.print((char) barray[i]);
//					out.write(barray[i]);
//		 		 }
//				 out.close();		 		 
//				}
//        
//				System.out.println("\nwaiting for HTTP response ...\n");
//
//				try {
//					System.out.println("Response data:\n");
//					
//					if(System.getProperty("java.version").startsWith("1.4")){
//					 Method getHeaderFields = HttpURLConnection.class.getMethod("getHeaderFields", null);
//					 Map headers = (Map)getHeaderFields.invoke(((HttpURLConnection) hurlconn), null);
//					 Set keys = headers.keySet();
//					 Iterator itkeys = keys.iterator();
//
//					  while(itkeys.hasNext()){					
//					   Object keyloc = itkeys.next();					   
//					   System.out.println((keyloc != null)?("Header: " + keyloc + " | Value: " + headers.get(keyloc)): headers.get(keyloc));						
//					  }					
//					}
//          else if(System.getProperty("java.version").startsWith("1.3"))
//           for(int i=0; i <100 ; i +=1){
//					  String hfield = ((HttpURLConnection) hurlconn).getHeaderField(i);
//					  String hfieldkey = ((HttpURLConnection) hurlconn).getHeaderFieldKey(i);
//					   if(hfield != null | hfieldkey != null)
//					    System.out.println((hfieldkey != null)?("Header: " + hfieldkey + " | Value: " + hfield): hfield);
//					 }
//
//					System.out.println("\nContent-Encoding: " + hurlconn.getContentEncoding());
//					System.out.println("URL: " + ur.toString().trim() + "\n");
//
//          System.out.println("Content of response:\n"); 
//   				//obj = hurlconn.getContent();
//					InputStream is = hurlconn.getInputStream();
//
//					while (true) {
//						int t = is.read();
//						if (t == -1)
//							break;
//						System.out.print((char) t);
//						count += 1;
//					}
//             
//				}
//				catch (Exception e) {
//					System.out.println("\nTransmission error: \n");
//					e.printStackTrace();
//				}
//				// out.close();
//			}
//		 System.out.println("\n\nEnd of monitoring. Response content size: " + getSizeAndUnit(count, 3));
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		//System.out.println("\n\nEnd of monitoring. Response content size: " + getSizeAndUnit(count, 3));
//	}
	
	public void printOut(String strg, ByteArrayOutputStream out){
		
	 //System.out.print(strg);
	 byte[] barray = strg.getBytes();
	 
	 //try{
	  for(int i = 0; i < barray.length; i += 1)
	   out.write(barray[i]);	 		
	 //}		
	 //catch(IOException e){
	 //} 
	}

	public void printOut(char chr, ByteArrayOutputStream out){		
	
	 out.write(chr);	 		
	}

	public void printlnOut(String strg, ByteArrayOutputStream out){
		
   String strgnl = strg + "\n";
   printOut(strgnl, out);
	}
	
	public ByteArrayOutputStream getResponseData(Document scenarioDoc, String fullurl) {

    String userPwd="";
		String encodedUser="";
		boolean baseEncoding = false;
		int count = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

    fullurl = fullurl.trim();
				
		if(fullurl.startsWith("http://")){
			try{
		   userPwd = fullurl.substring(7).substring(0,fullurl.substring(7).indexOf('@'));
		   baseEncoding = true;
			}
			catch(Exception e){
			 userPwd = "";
			}
		}
		if(fullurl.startsWith("https://")){
  	 try{
		  userPwd = fullurl.substring(8).substring(0,fullurl.substring(8).indexOf('@'));
			baseEncoding = true;
  	 }
  	 catch(Exception e){
  	 	userPwd = "";
  	 }
		}
   	
   	if(baseEncoding && !userPwd.equals("")){
   		if(userPwd.indexOf(':') == -1 || userPwd.indexOf(':') == userPwd.length() - 1 ||userPwd.indexOf(':') == 0){
   			printlnOut("Syntax error: " + userPwd, bout);
				printlnOut("<user>:<password> expected in tag scenstarturl!", bout);
   			return bout;
   		}
		 encodedUser = new BASE64Encoder().encode(userPwd);
   	}   	

   try{
		URL ur = new URL(fullurl);
   	URLConnection hurlconn = ur.openConnection();

		if (hurlconn instanceof HttpURLConnection) {
		 ((HttpURLConnection) hurlconn).setDoOutput(true);
		 ((HttpURLConnection) hurlconn).setDoInput(true);
		 ((HttpURLConnection) hurlconn).setRequestProperty("Content-Type", "text/html");

		  if(baseEncoding && !userPwd.equals(""))
		   ((HttpURLConnection) hurlconn).setRequestProperty("Authorization", "Basic " + encodedUser);

  	  ((HttpURLConnection) hurlconn).setRequestMethod("POST");

			printlnOut("Start getting connection (POST request) to", bout);
      printlnOut(ur.toString(), bout);
      
			if(baseEncoding && !userPwd.equals(""))
			 printlnOut("using basic authentication for " + userPwd, bout);

      DocumentNavigator docnav = new DocumentNavigator(scenarioDoc);
		  ByteArrayOutputStream docBOs = docnav.getDocumentAsOutputStream();			  
		  OutputStream out = hurlconn.getOutputStream();
      docBOs.writeTo(out);
		  out.close();		 		 
        
 			printlnOut("\nResponse data:\n", bout);
					
			if(System.getProperty("java.version").startsWith("1.4")){
			 Method getHeaderFields = HttpURLConnection.class.getMethod("getHeaderFields", null);
			 Map headers = (Map)getHeaderFields.invoke(((HttpURLConnection) hurlconn), null);
			 Set keys = headers.keySet();
			 Iterator itkeys = keys.iterator();

			 while(itkeys.hasNext()){					
			  Object keyloc = itkeys.next();
				String header1 = (keyloc != null)?("Header: " + keyloc + " | Value: " + headers.get(keyloc).toString()): headers.get(keyloc).toString();					   
				printlnOut(header1 , bout);						
			 }					
			}
			else if(System.getProperty("java.version").startsWith("1.3"))
			 for(int i=0; i <100 ; i +=1){
				String hfield = ((HttpURLConnection) hurlconn).getHeaderField(i);
				String hfieldkey = ((HttpURLConnection) hurlconn).getHeaderFieldKey(i);
				 if(hfield != null | hfieldkey != null){
					String header2 = (hfieldkey != null)?("Header: " + hfieldkey + " | Value: " + hfield): hfield;
					printlnOut(header2, bout);
				 }
			 }

			printlnOut("\nContent-Encoding: " + hurlconn.getContentEncoding(), bout);
			printlnOut("URL: " + ur.toString() + "\n", bout);
			printlnOut("Content of response:\n", bout); 

			InputStream is = hurlconn.getInputStream();

      /*
      try{
       // neu:					
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       //factory.setIgnoringElementContentWhitespace(true);	
       DocumentBuilder builder = factory.newDocumentBuilder();
       Document m_document = builder.parse(is);
      
       GrmgXMLFile gFile = new GrmgXMLFile(m_document);
       ByteArrayOutputStream boutXml = gFile.getDocumentAsOutputStream();
       count = boutXml.size();
       boutXml.writeTo(bout);
      }
			catch (ParserConfigurationException e) {
				printlnOut("ERROR: ParserConfigurationException " + e.toString(), bout);
				 while (true){
					int t = is.read();
					 if (t == -1)
						break;
					printOut((char) t, bout);
					count += 1;
				 }   
			}
			catch (SAXException e) {
				printlnOut("ERROR: SAXException " + e.toString(), bout);			
				 while (true){
					int t = is.read();
					 if (t == -1)
						break;
					printOut((char) t, bout);
					count += 1;
				 }   
			}			   
		  is.close();
		  */  
 
			while (true) {
			 int t = is.read();
				if (t == -1)
				 break;
			 printOut((char) t, bout);
			 count += 1;
			}   			 
		 }
	   printlnOut("\n\nEnd of monitoring. GRMG response content size: " + getSizeAndUnit(count, 3), bout);
	  }
		catch (MalformedURLException e) {
			printlnOut("ERROR: MalformedURLException " + e.toString(), bout);
			e.printStackTrace();
		}
		catch (ProtocolException e) {
			printlnOut("ERROR: ProtocolException " + e.toString(), bout);
			e.printStackTrace();
		}
		catch (IOException e) {
			printlnOut("ERROR: IOException " + e.toString(), bout);
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			printlnOut("ERROR: NoSuchMethodException " + e.toString(), bout);
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			printlnOut("ERROR: InvocationTargetException " + e.toString(), bout);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			printlnOut("ERROR: IllegalAccessException " + e.toString(), bout);
			e.printStackTrace();
		}
   return bout;
	}
	
  static void setProperties(String[] arg){
  	  	
  	for(int j = 0; j < arg.length ; j +=1){
  		props.setProperty(arg[j].substring(0,arg[j].indexOf('=')), 
  		                  arg[j].substring(arg[j].indexOf('=')+1));  		
  	}
  }  
 
 public static String getSizeAndUnit(int size, int digits){
 	
	  String unit = "";
		int helpcount = 0;
		String countString = "";
  	double unitcount = 0; 		         
  	int digitpower = (int)Math.pow(10,digits);
   
		if(size < 1024){	
		 countString = new Integer(size).toString();
		 unit = (size == 1)?" byte":" bytes";
		}
		else 
		 if(size < 1048576){ 
			unitcount = (double)size/1024;
			helpcount = (int)(digitpower * unitcount);
			unitcount = (double)helpcount/digitpower;
			countString = new Double(unitcount).toString();
			unit = " kB";  
		 }
		 else 
			if(size < 1073741824){
			 unitcount = (double)size/1048576;
			 helpcount = (int)(digitpower * unitcount);
			 unitcount = (double)helpcount/digitpower;			
			 countString = new Double(unitcount).toString();
			 unit = " MB";
			}  
			else{
			 unitcount = (double)size/1073741824;
			 helpcount = (int)(digitpower * unitcount);
			 unitcount = (double)helpcount/digitpower;
			 countString = new Double(unitcount).toString();
			 unit = " GB";
			}
	return countString + unit;
 }
}
