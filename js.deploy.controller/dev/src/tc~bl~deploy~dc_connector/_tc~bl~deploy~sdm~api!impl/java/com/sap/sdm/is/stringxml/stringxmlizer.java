package com.sap.sdm.is.stringxml;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public interface StringXMLizer {

	public void addElemWithContent(String name, String content);

	public void addElemWithContentCDATA(String name, String content);

	public void addElemWithAttrAndContent(String name, String attrName,
			String attrValue, String content);

	public void addElemWithAttrsAndContent(String name, String[] attrNames,
			String[] attrValues, String content);

	public void startElem(String name);

	public void startElemWithAttr(String name, String attrName, String attrValue);

	public void startElemWithAttrs(String name, String[] attrNames,
			String[] attrValues);

	public void endCurrentElem();

	public void endRootElem();

	public String getString();

}
