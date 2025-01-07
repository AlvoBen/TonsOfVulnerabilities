/*
 *  last change 2003-11-10
 */

package com.sap.util.monitor.grmg;
//package com.sap.util.monitor.grmg;
/**
 * Title:        GRMG Message
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 */
/**
<dd>This class 
is a wrapper for message type of :</dd><font COLOR="#0000ff">
<p><b>&lt;</b></font><b><font COLOR="#800000">message</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messalert</font><font COLOR="#0000ff">&gt;</font>OKAY
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messalert</font></b><font COLOR="#0000ff"><b>&gt;&nbsp;&nbsp; 
- message alert</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messseverity</font><font COLOR="#0000ff">&gt;</font>0
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messseverity</font></b><font COLOR="#0000ff"><b>&gt;&nbsp; 
- message severity</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messarea</font><font COLOR="#0000ff">&gt;</font>RT
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messarea</font></b><font COLOR="#0000ff"><b>&gt;&nbsp; 
-message area (message class in R/3 system)</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messnumber</font><font COLOR="#0000ff">&gt;</font>700
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messnumber</font><font COLOR="#0000ff">&gt; 
- message number in message class</font></b></p>
<font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam1</font><font COLOR="#0000ff">&gt;
</font>first<font COLOR="#0000ff"> &lt;/</font><font COLOR="#800000">messparam1</font></b><font COLOR="#0000ff"><b>&gt; 
- parameter </b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam2</font><font COLOR="#0000ff">&gt;</font>second
<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam2</font><font COLOR="#0000ff">&gt; 
- parameter</font></b></p>
<font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam3</font><font COLOR="#0000ff">&gt;</font>third<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam3</font><font COLOR="#0000ff">&gt; 
- parameter</font></b></p>
<font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messparam4</font><font COLOR="#0000ff">&gt;</font>fourth<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messparam4</font><font COLOR="#0000ff">&gt; 
- parameter</font></b></p>
<font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">messtext</font><font COLOR="#0000ff">&gt;</font>additional 
text<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">messtext</font></b><font COLOR="#0000ff"><b>&gt; 
additional text</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">message</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font>
<dt>&nbsp;</dt>
</DL>
*/
public class GrmgMessage
{
	
	private String messalert="";
	private String messseverity="";
	private String messarea="";
	private String messageId="";
	//private String messnumber="";
	private String messparam1="";
	private String messparam2="";
	private String messparam3="";
	private String messparam4="";
	private String messtext="";
	
	/**
	Maximum severity of a message
	*/
	public static String ERROR_SEVERITY_MAX = "255";
	/**
	Minimum severity of a message
	*/
	public static String ERROR_SEVERITY_MIN = "0";
	/**
	Default severity of a message
	*/
	public static String ERROR_SEVERITY_DEFAULT = "50";
	/**
	Error alert
	*/
	public static String MESSAGE_ALERT_ERROR = "ERROR";
	/**
	Notification alert
	*/
	public static String MESSAGE_ALERT_NOTIFICATION = "NA";
	/**
	Okay alert
	*/
	public static String MESSAGE_ALERT_OKAY = "OKAY";
	/**
	Message class
	*/
	public static String R3_MESSAGE_CLASS = "RT";
	/**
	Message 'Value is not used'
	*/
	public static String XML_VALUE_NOT_USED = "Not Used";
	
	
	/**
	Creates new grmgMessage object
	*/
	public GrmgMessage()
	{
	}
	/**
	Fills the message class with specified values of
	message alert, message severity, message area (R/3 message class), 
	message number in R/3 message class,
	message parameter No 1 for message in R/3 message class,
	message parameter No 2 for message in R/3 message class,
	message parameter No 3 for message in R/3 message class,
	message parameter No 4 for message in R/3 message class,								 
	message text 
	*/
	public void setMessageParameters(String messalert,
									 String messseverity,
									 String messarea,
									 String messnumber,
									 String messparam1,
									 String messparam2,
									 String messparam3,
									 String messparam4,
									 String messtext)
	{
		this.messalert=messalert;
		this.messseverity=messseverity;
		this.messarea=messarea;
		//this.messnumber=messnumber;
		this.messageId=messnumber;
		this.messparam1=messparam1;
		this.messparam2=messparam2;
		this.messparam3=messparam3;
		this.messparam4=messparam4;
		this.messtext=messtext;
		
	}
	/**
	Fills the message class with the values from 9 elements array
	*/
	public void setMessageParameters(String st[])
	{
		this.messalert=st[0];
		this.messseverity=st[1];
		this.messarea=st[2];
//		this.messnumber=st[3];
		this.messageId=st[3];
		this.messparam1=st[4];
		this.messparam2=st[5];
		this.messparam3=st[6];
		this.messparam4=st[7];
		this.messtext=st[8];
		
	}
	/**
	Returns message structure in form of 9 elements array
	*/
	public String[] getMessageParameters()
	{
		String st[]={
		messalert,
		messseverity,
		messarea,
//		messnumber,
		messageId,
		messparam1,
		messparam2,
		messparam3,
		messparam4,
		messtext};
		return st;
		
	}
	
	/**
	Returns message parameter 1
	*/
	public String getMessageParameter1()
	{
		return messparam1;
	}

	/**
	Returns message parameter 2
	*/
	public String getMessageParameter2()
	{
		return messparam2;
	}
	
	/**
	Returns message parameter 3
	*/
	public String getMessageParameter3()
	{
		return messparam3;
	}

	/**
	Returns message parameter 4
	*/
	public String getMessageParameter4()
	{
		return messparam4;
	}
	
	
	/**
	Sets message parameter 1
	*/
	public void setMessageParameter1(String value)
	{
		messparam1 = value;
	}

	/**
	Sets message parameter 2
	*/
	public void setMessageParameter2(String value)
	{
		messparam2 = value;
	}
	
	/**
	Sets message parameter 3
	*/
	public void setMessageParameter3(String value)
	{
		messparam3 = value;
	}

	/**
	Sets message parameter 4
	*/
	public void setMessageParameter4(String value)
	{
		messparam4 = value;
	}
	/**
	Returns message alert
	*/
	public String getMessageAlert()
	{
		return messalert;
	}
	
	/**
	Sets message alert
	*/
	public void setMessageAlert(String value)
	{
		messalert = value;
	}
	
	/**
	Returns message area
	*/
	public String getMessageArea()
	{
		return messarea;
	}
	
	/**
	Sets message area
	*/
	public void setMessageArea(String value)
	{
		messarea = value;
	}
	
	/**
	Returns message number
	*/
	public String getMessageId()
	{
//		return messnumber;
		return messageId;
	}
	
	/**
	Sets message number
	*/
	public void setMessageId(String value)
	{
//		messnumber = value;
		messageId = value;
	}
	
	/**
	Returns message severity
	*/
	public String getMessageSeverity()
	{
		return messseverity;
	}
	
	/**
	Sets message severity
	*/
	public void setMessageSeverity(String value)
	{
		messseverity = value;
	}
	
	/**
	Returns message text
	*/
	public String getMessageText()
	{
		return messtext;
	}
	
	/**
	Sets message text
	*/
	public void setMessageText(String value)
	{
		messtext = value;
	}
}