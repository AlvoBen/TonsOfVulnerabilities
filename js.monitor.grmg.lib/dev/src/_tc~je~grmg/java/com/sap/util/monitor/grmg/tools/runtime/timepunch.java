/*
 *  last change 2004-01-08
 */

/*
 * Author d031360
 * Created on 18.12.2003
 *
 */
package com.sap.util.monitor.grmg.tools.runtime;

import java.util.*;

public class TimePunch {

	public static String convert(int number, int digits){
 	
	 if(number >= Math.pow(10,digits))
		return new Integer(number).toString();
		
	 else{
		int lg = 0;
		String lgString = "0";
 	  
		 if(number != 0){
			lg = (int)(Math.log(number)/Math.log(10));
			lgString = new Integer(number).toString();
		 } 
	   
		 for(int j = 1; j <= digits - lg -1; j += 1)
			lgString = "0" + lgString;
 		
		return lgString;   
	 }
	}

	public static String getDate(){
  
	 GregorianCalendar gregor = new GregorianCalendar();
	 return convert(gregor.get(GregorianCalendar.YEAR),4) + 
					convert(gregor.get(GregorianCalendar.MONTH) + 1, 2) + 
					convert(gregor.get(GregorianCalendar.DAY_OF_MONTH), 2);
	}

	public static String getTime(){
  
	 GregorianCalendar gregor = new GregorianCalendar();
	 return convert(gregor.get(GregorianCalendar.HOUR_OF_DAY), 2) + 
					convert(gregor.get(GregorianCalendar.MINUTE), 2) +
					convert(gregor.get(GregorianCalendar.SECOND), 2);
	}
   
}
