/**************************************************************************** 
Apex Class Name  : UnifiedSearchWindow_C_HUM
Version          : 1.0
Created Date     : 12/02/2020
Function         : controller of UnifiedSearchWindow_VF_HUM Visualforce page
Test class       : UnifiedSearchWindow_T_HUM
*****************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Akshay Pai                                   12/02/2020                 original Version
***************************************************************************************************/
public with sharing class UnifiedSearchWindow_C_HUM {
    
    @RemoteAction
    public static void logCoveoError(string status,string errorMessage)
    {
        try
        {
            Error_Log__c errorLog = new Error_Log__c();
            errorLog = new Error_Log__c(Error_Code__c =status,
                                        Error_Message__c = errorMessage,
                                        Request_Sent__c = true,
                                        class_name__c = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHWINDOW,
                                        Method_Name__c = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_LOGCOVEOERROR,
                                        Type__c = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_APPLICATIONERROR);        
            database.insert(errorLog);      
        } 
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHWINDOW,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_LOGCOVEOERROR);
        }      
    } 
}