package com.sap.util.monitor.grmg;
import java.util.*;

/**
 * Title:        GRMG Scenario
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * @author       Pavel Kojevnikov
 * @version 1.1
 * @author Georgi Mihailov i026851
 * @version 640
 */

/**
This class is a wrapper for GRMG scenario defined in  XML format 
*/
public class GrmgScenario
{
  //{{ MP
  public static final String SCENARIO_TYPE_URL = "URL";
  public static final String SCENARIO_TYPE_HRFC = "HRFC";
  //}} MP

	private String scenario_name="";
	private String version="";
	private String instance_number="";
	private int number_of_components;

  //{{ MP
  private String type = SCENARIO_TYPE_URL;
  private String startUrl = "";
  private String startMode = "Unknown";

  //{{ i026851
  //private ArrayList texts = null;
  private GrmgText scenarioText = new GrmgText();
  
  private String clientSID = "";
  private String clientServer = "";
  //}} i026851

  //}} MP


	private int current;
	private ArrayList components=new ArrayList();
	/**
	creates new GrmgScenario object
	*/
	public GrmgScenario()
	{
	}

  //{{ MP
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStartUrl() {
    return startUrl;
  }

  public void setStartUrl(String startUrl) {
    this.startUrl = startUrl;
  }

  public String getStartMode() {
    return startMode;
  }

  public void setStartMode(String startMode) {
    this.startMode = startMode;
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
    return scenarioText;
  }

  public boolean setText(GrmgText componentText) {
    if (componentText == null) {
      return false;
    }

    this.scenarioText = componentText;
    return true;
  }
  
  public void setClientSID(String clientSID) {
    if (clientSID != null) {
      this.clientSID = clientSID;
    }
  }
  
  public String getClientSID() {
    return clientSID;
  }
  
  public void setClientServer(String clientServer) {
    if (clientServer != null) {
      this.clientServer = clientServer;
    }
  }
  
  public String getClientServer() {
    return clientServer;
  }   
  //}} i026851

  public ArrayList getComponents() {
    return components;
  }

  public void setComponents(ArrayList components) {
    this.components = components;
  }
  //}} MP


	/**
	sets the name of  scenario
	*/
	public void setName(String n)
	{
		scenario_name=n;
	} 
	/**
	sets the version of scenario
	*/
	public void setVersion(String n)
	{
		version=n;
	} 
	/**
	sets the instance of scenario
	*/
	public void setInstance(String n)
	{
		instance_number=n;
	} 
	
	/**
	returns the name of scenario
	*/	
	public String getName()
	{
		return scenario_name;
	} 
	/**
	returns version of scenario
	*/
	public String  getVersion()
	{
		return version;
	} 
	/**
	returns the instance of scenario
	*/
	public String  getInstance()
	{
		return instance_number;
	}
	/**
	adds new component to scenario
	*/
	public GrmgComponent  addComponent()
	{
		
		current=++number_of_components;
		GrmgComponent gcomp=new GrmgComponent();
		components.add(current-1,gcomp ) ;
		return gcomp;
		
	}
	
	
	/**
	adds component to scenario
	*/
	public void addComponent(GrmgComponent newGrmgComponent)
	{
		
		current=++number_of_components;
		components.add(current-1,newGrmgComponent) ;
		
		
	}
	
	
	/**
	returns current component class of scenario
	*/
	public GrmgComponent  getCurrentComponent()
	{
		return (GrmgComponent) components.get(current-1);
	}
	/**
	returns number of component classes associated with scenario
	*/
	public int getNumberOfComponents()
	{
		return number_of_components;
	}
	/**
	 returns i-th component class associated with scenario
	*/
	public GrmgComponent  getComponent(int i)
	{
		if(i<number_of_components)
		return (GrmgComponent) components.get(i);
		else
		return null;
	}
	
	/**
	 returns component class with specified name associated with scenario
	*/
	
	public GrmgComponent  getComponentByName(String st)
	{
		for (int i=0;i<number_of_components;i++)
		if(((GrmgComponent) components.get(i)).getName().equals(st))
		return (GrmgComponent) components.get(i);
		
		return null;
		
	}
	
}