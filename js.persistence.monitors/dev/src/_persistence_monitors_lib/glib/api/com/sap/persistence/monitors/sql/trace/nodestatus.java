package com.sap.persistence.monitors.sql.trace;







public interface NodeStatus  {
   
    
    
    public String getCurrentPrefix(); 
    public boolean isOn(); 

    public void setCurrentPrefix(String prefix);

    public boolean isStackTrace(); 

    public String getMethodPattern(); 
     
    public void setStackTrace(boolean b);  

    public void setMethodPattern(String mPat); 

    public long getThreshold();  
    
    public void setThreshold(long i) ; 
}