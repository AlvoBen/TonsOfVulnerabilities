/*
 *  last change 2004-03-19
 */

 
package com.sap.util.monitor.grmg;
import java.util.*;
/**
 * Title:        GRMG Component
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 */


/**
<dd>This class 
is a wrapper for GRMG Component defined in GrmgRequest object:</dd>
<dt>&nbsp;</dt>
<font COLOR="#0000ff">
<p><b>&lt;</b></font><b><font COLOR="#800000">component</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;</font><font COLOR="#800000">compname</font><font COLOR="#0000ff">&gt;</font>LOGON 
PAGE OF OTHER SERVER<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">compname</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;</font><font COLOR="#800000">compversion</font><font COLOR="#0000ff">&gt;</font>987<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">compversion</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">properties</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">propname</font><font COLOR="#0000ff">&gt;</font>url<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propname</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">propvalue</font><font COLOR="#0000ff">&gt;</font>http://localhost:1080/test<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propvalue</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;/</font><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">propname</font><font COLOR="#0000ff">&gt;</font>user<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propname</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">propvalue</font><font COLOR="#0000ff">&gt;</font>hi<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propvalue</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;/</font><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font>
<p><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;/</font><font COLOR="#800000">properties</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">component</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font>
<dt><b>or GrmgResponse:</b></dt>
<dd><font COLOR="#0000ff"><b>&lt;</b></font><b><font COLOR="#800000">component</font></b><font COLOR="#0000ff"><b>&gt;</b></font><font SIZE="1"><p>
</font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp; &lt;</font><font COLOR="#800000">compname</font><font COLOR="#0000ff">&gt;</font>tst<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">compname</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">compversion</font><font COLOR="#0000ff">&gt;</font>001
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">compversion</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">comphost</font><font COLOR="#0000ff">&gt;</font>host
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">comphost</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">compinst</font><font COLOR="#0000ff">&gt;</font>instance
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">compinst</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messages</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">message</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messalert</font><font COLOR="#0000ff">&gt;</font>OKAY
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messalert</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messseverity</font><font COLOR="#0000ff">&gt;</font>0
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messseverity</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messarea</font><font COLOR="#0000ff">&gt;</font>RT
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messarea</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messnumber</font><font COLOR="#0000ff">&gt;</font>700
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messnumber</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messparam1</font><font COLOR="#0000ff">&gt;</font>string1<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam1</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</font><b><font COLOR="#0000ff">&nbsp; &lt;</font><font COLOR="#800000">messparam2</font><font COLOR="#0000ff">&gt;</font>string2<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam2</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam3</font><font COLOR="#0000ff">&gt;</font>string3<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam3</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam4</font><font COLOR="#0000ff">&gt;</font>string4<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam4</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;</font><font COLOR="#800000">messtext</font><font COLOR="#0000ff">&gt;</font>any 
text<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messtext</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</font><b><font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">message</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
&lt;/</font><font COLOR="#800000">messages</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">component</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font>
<p>&nbsp;</dd>
<dd>&nbsp;</dd>
</DL>


*/
public class GrmgComponent
{
	
	private String component_name="";
	private String version="";
	private String comphost="";
	private String compinst="";
	
	private int number_of_properties;
	
	
	private int number_of_messages;
	
	private int current;
	
	private int curr_mess;
	
	
	
	ArrayList properties=new ArrayList();
	
	
	ArrayList messages=new ArrayList();

  //{{ MP
  private String type = "Unknown";

  //{{ i026851
  //private ArrayList texts = null;
  private GrmgText componentText = new GrmgText();
  //}} i026851

  //}} MP

	/**
	Creates new empty Grmg component
	*/
	public GrmgComponent()
	{
	}

  //{{ MP
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  //}} i026851
  
  /*public ArrayList getTexts() {
    return texts;
  }

  public void setTexts(ArrayList texts) {
    this.texts = texts;
  }

  public boolean addText(GrmgText text) {
    if (texts == null) {
      texts = new ArrayList();
    }
    return texts.add(text);
  }*/

  public GrmgText getText() {
    return componentText;
  }

  public boolean setText(GrmgText componentText) {
    if (componentText == null) {
      return false;
    }

    this.componentText = componentText;
    return true;
  }
  //}} i026851

  public ArrayList getProperties() {
    return properties;
  }

  public void setProperties(ArrayList properties) {
    this.properties = properties;
  }
  //}} MP


	/**
	returns the name of component
	*/
	public String getName()
	{
		return component_name;
	} 
	/**
	returns version of component
	*/
	public String  getVersion()
	{
		return version;
	} 
	
	/**
	sets the name of component
	*/
	public void setName(String n)
	{
		component_name=n;
	} 
	/**
	sets version of component
	*/
	public void setVersion(String n)
	{
		version=n;
	}
	
	
	/**
	returns host parameter of component
	*/
	public String getHost()
	{
		return comphost;
	} 
	/**
	returns instance parameter of component
	*/
	public String  getInst()
	{
		return compinst;
	} 
	
	/**
	sets host name associated with component
	*/
	public void setHost(String n)
	{
		comphost=n;
	} 
	/**
	sets instance associated with component 
	*/
	public void setInst(String n)
	{
		compinst=n;
	}
	
	
	/**
	returns number of property class objects associated with component
	*/
	public int getNumberOfProperties()
	{
		return number_of_properties;
	}
	/**
	returns number of message class objects associated with component
	*/
	public int getNumberOfMessages()
	{
		return number_of_messages;
	}
	/**
	Adds new GrmgProperty class to component
	*/
	public GrmgProperty addProperty()
	{
		current=++number_of_properties;
		GrmgProperty gp=new GrmgProperty();
		properties.add(current-1, gp) ;
		return gp;
	}
	
	/**
	Adds GrmgProperty class to component
	*/
	
	public void addProperty(GrmgProperty newGrmgProperty)
	{
		current=++number_of_properties;
		properties.add(current-1, newGrmgProperty) ;
		
	}
	
	/**
	Adds new GrmgMessage class to component
	*/
	public GrmgMessage addMessage()
	{
		curr_mess=++number_of_messages;
		GrmgMessage gm=new GrmgMessage();
		messages.add(curr_mess-1, gm) ;
		return gm;
	}
	
	/**
	Adds GrmgMessage class to component
	*/
	public void addMessage(GrmgMessage newGrmgMessage)
	{
		curr_mess=++number_of_messages;
		messages.add(curr_mess-1, newGrmgMessage) ;
		
	}
	
		
	/**
	returns current property class object
	*/
	public GrmgProperty  getCurrentProperty()
	{
		return (GrmgProperty) properties.get(current-1);
	}
	
	/**
	 returns current message class object
	*/
	public GrmgMessage  getCurrentMessage()
	{
		return (GrmgMessage) messages.get(curr_mess-1);
	}
	
	/**
	returns the i-th property class object
	*/
	public GrmgProperty  getProperty(int i)
	{
		if(i<number_of_properties)
		return (GrmgProperty) properties.get(i);
		return null;
	}
	
	/**
	returns the i-th message class from array of message classes
	*/
	public GrmgMessage  getMessage(int i)
	{
		if(i<number_of_messages)
		return (GrmgMessage) messages.get(i);
		return null;
	}
	
	
	/**
	returns the property class object with specified name
	*/
	public GrmgProperty  getPropertyByName(String st)
	{
		for (int i=0;i<number_of_properties;i++)
		if(((GrmgProperty) properties.get(i)).getName().equals(st))
		return (GrmgProperty) properties.get(i);
		
		return null;
	}
	
}