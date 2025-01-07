/*
 *  last change 2004-01-08
 */

package com.sap.util.monitor.grmg.tools.runtime;

import java.util.*;

public class RuntimeArguments {

  private String[] innerArgs;
  
  public RuntimeArguments(String[] args){
  
   innerArgs = args; 	  	
  }
  
	public Properties getProperties(String[] arg){
  	  	
		Properties props = new Properties();
   	  	
		for(int j = 0; j < arg.length ; j +=1){
      if(arg[j].indexOf("=") > -1)
			 props.setProperty(arg[j].substring(0,arg[j].indexOf('=')), 
			  								 arg[j].substring(arg[j].indexOf('=')+1));  		
		}
		return props;
	}  

	public ArrayList getPureArguments(String[] arg){
  	  	
		ArrayList arrlist = new ArrayList();
   	  	
		for(int j = 0; j < arg.length ; j +=1){
     if(arg[j].indexOf('=') == -1)
      arrlist.add(arg[j]);
		}
		return arrlist;
	}  

	public ArrayList getAllArguments(String[] arg){
  	
  	return (ArrayList)Arrays.asList(arg);  	
	}
	
	public String getProperty(String propname){
	
	 return getProperties(innerArgs).getProperty(propname);
	}

	public boolean containsPureArgument(String argname){
	
	 return getPureArguments(innerArgs).contains(argname);
	}	  
}
