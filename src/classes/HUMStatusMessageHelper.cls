/**************************************************************************** 
Apex Class Name  : HUMStatusMessageHelper
Version          : 1.0  
Created Date     : May 9 2014 
Function         : It will used as a Base class for handling Error Status and messages 
				  Used without sharing as the service is exposed to external system
****************************************************************************/ 

global without sharing class HUMStatusMessageHelper{
    
    webservice Boolean bStatus{get; set;}
    webservice StatusCode errorCode{get; set;}
    webservice String  sErrorMessage{get; set;}
    
    global HUMStatusMessageHelper(Boolean eStatus, StatusCode eCode, String eMessage){
        
        bStatus= eStatus;
        errorCode    = eCode;
        sErrorMessage= eMessage;
    }

}