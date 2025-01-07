/*
 *  last change 2003-11-10 
 */

/**
 * @author Bernhard Drabant
 *
 */
package com.sap.util.monitor.grmg;

 /**
 <code>ScenarioPanelException</code> represents and covers the exceptions occuring
 during creation of a <code>ScenarioPanel</code> instance or while adding 
 <code>ScenarioDevice</code>s to the panel.
 */

public class ScenarioPanelException extends Exception{

 /*
 Constructors for ScenarioPanelException
 */
 public ScenarioPanelException(String msg){
 	
	super(msg);
 }

 public ScenarioPanelException(){

  super();		
 }
}
