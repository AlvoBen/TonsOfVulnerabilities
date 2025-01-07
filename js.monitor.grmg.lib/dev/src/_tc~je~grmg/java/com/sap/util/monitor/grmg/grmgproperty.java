/*
 *  last change 2003-11-10
 */

package com.sap.util.monitor.grmg;
//package com.sap.util.monitor.grmg;
/**
 * Title:        GRMG Property
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 */


/**
<dd>This class 
is a wrapper for GRMG Property defined in XML.</dd><font COLOR="#0000ff">
<p><b>&lt;</b></font><b><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">propname</font><font COLOR="#0000ff">&gt;</font>property 
name<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propname</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;</font><font COLOR="#800000">propvalue</font><font COLOR="#0000ff">&gt;</font>property 
value<font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">propvalue</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font><font SIZE="1">
<p></font><b><font COLOR="#0000ff">&lt;/</font><font COLOR="#800000">property</font></b><font COLOR="#0000ff"><b>&gt;</b></p>
</font>
<dt>&nbsp;</dt>
</DL>
*/
public class GrmgProperty
{
	private String name="";
	private String value="";
	/**
	Creates new GrmgProperty object
	*/
	public GrmgProperty()
	{
	}
	/**
	Returns the name of GrmgProperty object
	*/
	public String getName()
	{
		return name;
	} 
	/**
	Returns the value of GrmgProperty object
	*/
	public String getValue()
	{
		return value;
	}
	/**
	Sets name of GrmgProperty object
	*/
	public void setName(String n)
	{
		name=n;
	}
	/**
	Sets value of GrmgProperty object
	*/ 
	public void  setValue(String v)
	{
		value=v;
	}
}
