/******************************************************************************************************************************
Apex Class Name :  AuraErrorHandler_C_HUM
Version: 1.0 
Created Date     : 07/01/2019
Function         : Controller to handle searching of Visitors Or Members And corresponding results display.                   
Test Class       : AuraErrorHandler_T_HUM

        Modification Log : 
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Chetan Sindhe                                 07/01/2019          REQ-397261 Original Version
*********************************************************************************************************************************/
Public Class AuraErrorHandler_C_HUM {
    Public Class controllerException extends Exception {}
    
    Public Static AuraHandledException createAuraHandledException(String className, String methodName, String Message) {
        String errMessage = null;
        AuraHandledException e = null;
        Try {
            If (className == null) {
            	errMessage = Message;  
            }
            Else {
                //errMessage = 'Error in apex class ' + className + ' at method: ' + methodName + '. Error is ' + Message;
                errMessage = Message;
            }
            e = New AuraHandledException(errMessage);
            e.setMessage(errMessage);
             If (Test.isRunningTest())
            {
                Throw New controllerException();
            }
        }
         Catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'AuraErrorHandler_C_HUM', 'createAuraHandledException');
            Throw New AuraHandledException(ex.getMessage());
        }    
         Return e;
    }

}