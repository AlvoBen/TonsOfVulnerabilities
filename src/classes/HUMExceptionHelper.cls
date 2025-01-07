/*******************************************************************************************************************************
Apex Class Name : HUMExceptionHelper 
Version         : 1.0
Created On      : 05/21/2014
Function        : Controller which handles all types of exceptions
                  A detailed analysis was done on date '11/03/2017' by developer "Vijaya Koriprolu" on Sharing without option for this class.
        During the analysis it was found that this Is a generic exception Class, which Is used To show Error logs To every current user, restrictions to this class will break the functionality.
                  This was further discussed with Architects Amrish And Jeremy And got approval to keep the "Without Sharing" mode of this class as it Is.
Test Class      : HUMExceptionHelperTest
Modification Log: 
* Developer Name          Code Review              Date                           Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Venkat                  17152                     05/21/2014                     Original
* Chaitanya Kumar         17152                     06/25/2014                     Refactored entire code to add logic to handle different kind of exceptions
* Chaitanya Kumar         17152                     07/07/2014                     Moved Constants to HUMConstants class
* Chaitanya Kumar         17152                     08/08/2014                     Updated code to show Error message on page.Error messages are now displayed from Save log method
* Chaitanya Kumar         17267                     08/12/2014                     Updated addMessagesToPage method to remove duplicate messages on page
* Anil P                  17593                     08/27/2014                     Defect 150560.0001 to display error code with message
* Santhi Mandava          21968                    03/26/2015                     As per the REQ-02635, In case of batch execution irrespective of error code all 
                                                                                   errors should be inserted in error log object, so updated 'processUpsertResults' function to incorporate this.
* Sharan Shanmugam		  25907					    08/25/2015					   listBatchErrorLogs variable holds the Error logs from HUMCaseWorkQueueViewNameBatch and HUMTaskWorkQueueViewNameBatch
*********************************************************************************************************************************************************************************/
//Code should work irrespecive of profile settings as this handles exceptions across the application
public without sharing class HUMExceptionHelper 
{
    // Class constants
    private static final String SINGLE_LINE_DELIM = '\n';
    private static final String COLON = ':';
    private static final String DESC_LINE_DELIM = SINGLE_LINE_DELIM + SINGLE_LINE_DELIM;
    public static final string SEVERITY_WARNING = HUMConstants__c.getInstance('SEVERITY_WARNING') != null ? HUMConstants__c.getInstance('SEVERITY_WARNING').StringValue__c : '';
    public static final string SEVERITY_CONFIRM = HUMConstants__c.getInstance('SEVERITY_CONFIRM') != null ? HUMConstants__c.getInstance('SEVERITY_CONFIRM').StringValue__c : '';
    public static final string SEVERITY_ERROR = HUMConstants__c.getInstance('SEVERITY_ERROR') != null ? HUMConstants__c.getInstance('SEVERITY_ERROR').StringValue__c : '';
    public static final string SEVERITY_FATAL = HUMConstants__c.getInstance('SEVERITY_FATAL') != null ? HUMConstants__c.getInstance('SEVERITY_FATAL').StringValue__c : '';
    public static final string SEVERITY_INFO = HUMConstants__c.getInstance('SEVERITY_INFO') != null ? HUMConstants__c.getInstance('SEVERITY_INFO').StringValue__c : '';    
    // Start of List of valid of Exceptions
    public static final string DML_EXCEPTION = HUMConstants__c.getInstance('DML_EXCEPTION') != null ? HUMConstants__c.getInstance('DML_EXCEPTION').StringValue__c : '';
    public static final string NULLPOINTER_EXCEPTION = HUMConstants__c.getInstance('NULLPOINTER_EXCEPTION') != null ? HUMConstants__c.getInstance('NULLPOINTER_EXCEPTION').StringValue__c : '';
    public static final string QUERY_EXCEPTION = HUMConstants__c.getInstance('QUERY_EXCEPTION') != null ? HUMConstants__c.getInstance('QUERY_EXCEPTION').StringValue__c : '';
    public static final string SECURITY_EXCEPTION = HUMConstants__c.getInstance('SECURITY_EXCEPTION') != null ? HUMConstants__c.getInstance('SECURITY_EXCEPTION').StringValue__c : '';
    public static final string MATH_EXCEPTION = HUMConstants__c.getInstance('MATH_EXCEPTION') != null ? HUMConstants__c.getInstance('MATH_EXCEPTION').StringValue__c : '';
    public static final string CALLOUT_EXCEPTION = HUMConstants__c.getInstance('CALLOUT_EXCEPTION') != null ? HUMConstants__c.getInstance('CALLOUT_EXCEPTION').StringValue__c : '';
    public static final string JSON_EXCEPTION = HUMConstants__c.getInstance('JSON_EXCEPTION') != null ? HUMConstants__c.getInstance('JSON_EXCEPTION').StringValue__c : '';
    public static final string LIST_EXCEPTION = HUMConstants__c.getInstance('LIST_EXCEPTION') != null ? HUMConstants__c.getInstance('LIST_EXCEPTION').StringValue__c : '';
    public static final string TYPE_EXCEPTION = HUMConstants__c.getInstance('TYPE_EXCEPTION') != null ? HUMConstants__c.getInstance('TYPE_EXCEPTION').StringValue__c : '';
    public static final string INVALID_PARAMTER_EXCEPTION = HUMConstants__c.getInstance('INVALID_PARAMTER_EXCEPTION') != null ? HUMConstants__c.getInstance('INVALID_PARAMTER_EXCEPTION').StringValue__c : '';
    public static final string NO_ACCESS_EXCEPTION = HUMConstants__c.getInstance('NO_ACCESS_EXCEPTION') != null ? HUMConstants__c.getInstance('NO_ACCESS_EXCEPTION').StringValue__c : '';
    public static final string SEARCH_EXCEPTION = HUMConstants__c.getInstance('SEARCH_EXCEPTION') != null ? HUMConstants__c.getInstance('SEARCH_EXCEPTION').StringValue__c : '';
    public static final string SERIALIZATION_EXCEPTION = HUMConstants__c.getInstance('SERIALIZATION_EXCEPTION') != null ? HUMConstants__c.getInstance('SERIALIZATION_EXCEPTION').StringValue__c : '';
    public static final string XML_EXCEPTION = HUMConstants__c.getInstance('XML_EXCEPTION') != null ? HUMConstants__c.getInstance('XML_EXCEPTION').StringValue__c : '';
    public static final string SOBJECT_EXCEPTION = HUMConstants__c.getInstance('SOBJECT_EXCEPTION') != null ? HUMConstants__c.getInstance('SOBJECT_EXCEPTION').StringValue__c : '';
    public static final string CUSTOM_EXCEPTION = HUMConstants__c.getInstance('CUSTOM_EXCEPTION') != null ? HUMConstants__c.getInstance('CUSTOM_EXCEPTION').StringValue__c : '';
    //End of List of Valid Exceptions    
    //Constant to identiy Integration Eror
    public static final string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';   
    //Constant to Identity Application Error
    public static final string ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';    
    //Exception list that come under all Other Kind of exceptions
    public static final set < String > setOtherExceptionTypes = new Set < String > 
    {
        DML_EXCEPTION,
        NULLPOINTER_EXCEPTION,
        QUERY_EXCEPTION,
        SECURITY_EXCEPTION,
        MATH_EXCEPTION,
        CALLOUT_EXCEPTION,
        JSON_EXCEPTION,
        LIST_EXCEPTION,
        TYPE_EXCEPTION,
        INVALID_PARAMTER_EXCEPTION,
        NO_ACCESS_EXCEPTION,
        SEARCH_EXCEPTION,
        SERIALIZATION_EXCEPTION,
        XML_EXCEPTION,
        SOBJECT_EXCEPTION,
        CUSTOM_EXCEPTION
    };  
    //List of Valid Exceptions 
    public static final set < StatusCode > setValidExceptionCodes = new set < StatusCode > 
    {
        StatusCode.FIELD_CUSTOM_VALIDATION_EXCEPTION
    };
    //List of Valid Error Severity types
    public Static final Map < String, ApexPages.Severity > mapSeverityTypes = new Map < String, ApexPages.Severity > 
    {
            SEVERITY_WARNING => ApexPages.Severity.Warning,
            SEVERITY_CONFIRM => ApexPages.Severity.Confirm,
            SEVERITY_ERROR => ApexPages.Severity.Error,
            SEVERITY_INFO => ApexPages.Severity.Info,
            SEVERITY_FATAL => ApexPages.Severity.Fatal,
            null => ApexPages.Severity.Fatal
    };   
    //Flag to stop insertion of Error Log objects in case of Rollback / Constructor related exceptions
    public static boolean bRestrictLog = false;   
    //Dont show any Error Message on the page
    public static boolean bShowErrorOnPage = true;  
    public static boolean isBatchApex =false;
     
    public string getrandomString()
    {
        return 'N' + String.valueOf(Integer.valueOf(Math.random()*10000));
    }           
    //Store the list of Error Logs 
    public List < Error_Log__c > listErrorLogs 
    {
        get 
        {
            if (listErrorLogs == null) { return new List < Error_Log__c > (); }
            return listErrorLogs;
        }
        set;
    }
	
	public static List < Error_Log__c > listBatchErrorLogs = new List < Error_Log__c > ();

    /*
     * Method Name   :   getErrorDescription
     * Description   :   Get the error description from Error code look up object
     * Return Type   :   Error_Code_Lookup__c
     * Parameters    :   String
     */
    public static Error_code_Lookup__c getErrorDescription(String sErrorCode) 
    {
        if (!String.ISBlank(sErrorCode)) 
        {
            //Get the Generic Error Message based on the Error Code provided
            List < Error_code_Lookup__c > listErrorLookup = [select
                error_code__c,
                User_Friendly_Message__c,
                Description__c,
                severity__c
                from Error_code_Lookup__c
                where Error_code__c = : sErrorCode
            ];
            if (listErrorLookup != null && listErrorLookup.size() > 0) { return listErrorLookup[0]; }
        }
        //Return a generic error message in case of no records
        return new Error_code_lookup__c(User_Friendly_Message__c = Label.Error_Contact_Administrator, Severity__c = SEVERITY_FATAL);
    }

    /*
     * Method Name   :   logErrors
     * Description   :   Parse the Exception type and route the logic accordingly to initiate error log objects to insert in database
     * Return Type   :   List<Error_Log__c>
     * Parameters    :   String ,String , String
     */
    public static List < Error_Log__c > logErrors(System.Exception exceptionToLog, String sClassName, String sMethodName) 
    {
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        String sExceptionType = exceptionToLog.getTypeName();
        //Differentiate if the exception is Application Error / Integration Error 
        if (sExceptionType.equalsIgnoreCase(CUSTOM_EXCEPTION)) 
        {
            HUMCustomException customException = (HUMCustomException) exceptionToLog;
            
            if (HUMCustomException.getExceptionType(customException.exceptionWrapper) == ERROR_INTEGRATION) 
            {
                //Get the populated error log data
                Error_Log__c errorLog = logIntegrationErrors(exceptionToLog, sClassName, sMethodName);
                listErrorlogs.add(errorLog);
            } 
            else 
            {
                /*Dont log any error into database . Show the Error on the Page - 
                HUMCustomException is used for Application related errors to show any warning messages to End user.For instance , proper search criteria */
                addMessageToPage(ApexPages.Severity.Warning, exceptionToLog.getMessage(), bShowErrorOnPage);
            }
        } 
        else 
        {
            if (sExceptionType.equalsIgnoreCase(CALLOUT_EXCEPTION)) 
            {
                Error_Log__c errorLog = logCallOutExceptions(exceptionToLog, sClassName, sMethodName);
                listErrorlogs.add(errorLog);
            }
            //Process all other valid type of Exceptions
            else if (setOtherExceptionTypes.contains(sExceptionType)) 
            {
                Error_Log__c errorLog = logOtherExceptions(exceptionToLog, sClassName, sMethodName);
                listErrorlogs.add(errorLog);
            } 
            else 
            {
                Error_Log__c errorLog = logOtherExceptions(exceptionToLog, sClassName, sMethodName);
                listErrorlogs.add(errorLog);
            }
        }
        //Insert all the logs
        saveLog(listErrorLogs, bRestrictLog);
        return listErrorLogs;
    }

    /*
     * Method Name   :   logIntegrationErrors
     * Description   :   Populate Error Log data for Integration errors
     * Return Type   :   Error_Log__c
     * Parameters    :   String ,String , String
     */
    public static Error_Log__c logIntegrationErrors(System.Exception integrationException, String sClassName, String sMethodName) 
    {

        Error_Log__c errorLog = new Error_Log__c();
        if (integrationException != null) 
        {
            HUMCustomException customException = (HUMCustomException) integrationException;
            if (customException.exceptionWrapper != null) 
            {
                errorLog = new Error_Log__c(Error_Code__c = customException.exceptionWrapper.sErrorCode,
                Error_Message__c = customException.exceptionWrapper.sErrorDescription + DESC_LINE_DELIM + integrationException.getStackTraceString(),
                Request_Sent__c = true,
                class_name__c = sClassName,
                Method_Name__c = sMethodName,
                Type__c = ERROR_INTEGRATION);
            }
        }
        return errorLog;
    }

    /*
     * Method Name   :   logCallOutExceptions
     * Description   :   Populate Error Log data for Call out exceptions
     * Return Type   :   Error_Log__c
     * Parameters    :   String ,String , String
     */
    public static Error_Log__c logCallOutExceptions(System.Exception callOutException, String sClassName, String sMethodName) 
    {
        Error_Log__c errorLog = new Error_Log__c();
        if (callOutException != null) 
        {
            errorLog = new Error_Log__c(Error_Code__c = callOutException.getTypeName(),
                Error_Message__c = callOutException.getMessage() + DESC_LINE_DELIM + callOutException.getStackTraceString(),
                //As a call out exception , request is not sent
                Request_Sent__c = false,
                class_name__c = sClassName,
                Method_Name__c = sMethodName,
                Type__c = ERROR_INTEGRATION);
        }
        return errorLog;
    }

    /*
     * Method Name   :   processSaveResults
     * Description   :   Populate Error Log data for Insert / Update operation
     * Return Type   :   boolean
     * Parameters    :   List<Database.SaveResult> , List<SObject> , String , String , String
     */
    public static boolean processSaveResults(List < Database.SaveResult > listSaveResults, List < SObject > listSObjects, String sClassName, String sMethodName, String sObjectType) 
    {
        String sUserMessage = '';
        boolean noValidError = false;
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        if (listSaveResults != null) 
        {
            Database.Error error;
            for (Integer count = 0; count < listSaveResults.size(); count++) 
            {
                if (!listSaveResults[count].isSuccess()) 
                {
                    error = listSaveResults[count].getErrors()[0];
                    if (setValidExceptionCodes.contains(error.getStatusCode())) 
                    {
                        sUserMessage = sUserMessage + SINGLE_LINE_DELIM + error.getMessage();
                        noValidError = true;
                    } 
                    else 
                    {
                        listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(error.getStatusCode()),
                        Error_Message__c = error.getMessage(),
                        class_name__c = sClassName,
                        Method_Name__c = sMethodName,
                        Object_Type__c = sObjectType,
                        Record_ID__c = listSObjects[count].id,
                        Type__c = ERROR_APPLICATION));
                    }
                }
            }
        }
        //If Error List size is more than Zero , there are some exceptions generated. Post a generic message from Error look up table
        if (listErrorLogs.size() > 0) 
        {
            //Insert all the logs
            saveLog(listErrorLogs, bRestrictLog);
            return true;
        } 
        else 
        {
            //Messages are valid Warning messages which might to related to Validation rule , Add error methods  etc
            if (noValidError) { addMessageToPage(ApexPages.Severity.Warning, sUserMessage, bShowErrorOnPage); }
        }
        return false;
    }
    
    /*
     * Method Name   :   processUpsertResults
     * Description   :   Populate Error Log data for upsert operation
     * Return Type   :   boolean
     * Parameters    :   List<Database.upsertResult> , List<SObject> , String , String , String
     */
    public static boolean processUpsertResults(List < Database.UpsertResult > listUpsertResults, List < SObject > listSObjects, String sClassName, String sMethodName, String sObjectType) 
    {
        String sUserMessage = '';
        Boolean noValidError = false;
		listBatchErrorLogs = new List < Error_Log__c > ();
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        if (listUpsertResults != null) 
        {
            Database.Error error;
            for (Integer count = 0; count < listUpsertResults.size(); count++) 
            {
                if (!listUpsertResults[count].isSuccess()) 
                {
                    error = listUpsertResults[count].getErrors()[0];
                    if (setValidExceptionCodes.contains(error.getStatusCode()) && (!isBatchApex)) 
                    {
                        sUserMessage = sUserMessage + SINGLE_LINE_DELIM + error.getMessage();
                        noValidError = true;
                    } 
                    else 
                    {
                        listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(error.getStatusCode()),
                        Error_Message__c = error.getMessage(),
                        class_name__c = sClassName,
                        Method_Name__c = sMethodName,
                        Object_Type__c = sObjectType,
                        Record_ID__c = listSObjects[count].id,
                        Type__c = ERROR_APPLICATION));
                    }
                }
            }
        }
        //If Error List size is more than Zero , there are some exceptions generated. Post a generic message from Error look up table
        if (listErrorLogs.size() > 0) 
        {
            listBatchErrorLogs = listErrorLogs;
			//Insert all the logs
            saveLog(listErrorLogs, bRestrictLog);
            return true;
        } 
        else 
        {
            //Messages are valid Warning messages which might to related to Validation rule , Add error methods  etc
            if (noValidError) { addMessageToPage(ApexPages.Severity.Warning, sUserMessage, bShowErrorOnPage); }
        }
        return false;
    }

    /*
     * Method Name   :   processDeleteResults
     * Description   :   Populate Error Log data for Delete operation
     * Return Type   :   boolean
     * Parameters    :   List<Database.DeleteResult> , List<SObject> , String , String , String
     */
    public static boolean processDeleteResults(List < Database.DeleteResult > listDeleteResults, List < SObject > listSObjects, String sClassName, String sMethodName, String sObjectType) 
    {
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        String sUserMessage = '';
        Boolean noValidError = false;
        if (listDeleteResults != null) 
        {
            Database.Error error;
            for (Integer count = 0; count < listDeleteResults.size(); count++) 
            {
                if (!listDeleteResults[count].isSuccess()) 
                {
                    error = listDeleteResults[count].getErrors()[0];
                    if (setValidExceptionCodes.contains(error.getStatusCode())) 
                    {
                        sUserMessage = sUserMessage + SINGLE_LINE_DELIM + error.getMessage();
                        noValidError = true;
                    } 
                    else 
                    {
                        listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(error.getStatusCode()),
                        Error_Message__c = error.getMessage(),
                        class_name__c = sClassName,
                        Method_Name__c = sMethodName,
                        Object_Type__c = sObjectType,
                        Record_ID__c = listSObjects[count].id,
                        Type__c = ERROR_APPLICATION));
                    }
                }
            }
        }
        //If Error List size is more than Zero , there are some exceptions generated. Post a generic message from Error look up table
        if (listErrorLogs.size() > 0) 
        {
            //Insert all the logs
            saveLog(listErrorLogs, bRestrictLog);
            return true;
        } 
        else 
        {
            //Messages are valid Warning messages which might to related to Validation rule , Add error methods  etc
            if (noValidError) { addMessageToPage(ApexPages.Severity.Warning, sUserMessage, bShowErrorOnPage);}
        }
        return false;
    }

    /* Method Name   :    logOtherExceptions
     * Description   :    Log other kind of exceptions
     * Return Type   :    Error_Log__c
     * Parameters    :    Exception , String , String
     */
    public static Error_Log__c logOtherExceptions(System.Exception exceptionToLog, String sClassName, String sMethodName) 
    {
        Error_Log__c errorLog = new Error_Log__c();
        if (exceptionToLog != null) 
        {
            errorLog = new Error_Log__c(Error_Code__c = exceptionToLog.getTypeName(),
            Error_Message__c = exceptionToLog.getMessage() + DESC_LINE_DELIM + exceptionToLog.getStackTraceString(),
            class_name__c = sClassName,
            Method_Name__c = sMethodName,
            Type__c = ERROR_APPLICATION);
        }
        return errorLog;
    }

    /* Method Name   :    saveLog
     * Description   :    Save the Error logs . A Static method
     * Return Type   :    void
     * Parameters    :    List<Error_Log__c> , boolean
     */
    public static void saveLog(List < Error_Log__c > logsToSave, boolean bdontInsertLog) 
    {
        if (logsToSave != null && logsToSave.size() > 0 && !bdontInsertLog) 
        {
            List < Database.SaveResult > lstSaveResults = Database.insert(logsToSave, false);
            List < ID > lstErrorCodeIDs = new List < ID > ();
            for (Database.SaveResult saveResult: lstSaveResults) 
            {
                lstErrorCodeIDs.add(saveResult.getID());
            }
            //Get the list of records inserted
            List < Error_Log__c > lstErrorLogs = [Select Name, Error_Code__C, Object_Type__c from Error_Log__c where id in : lstErrorCodeIDs];
            //Loop through the list to add Error Messages
            for (Error_Log__c errorLog: lstErrorLogs) 
            {
                Error_code_Lookup__c errorLookup;
                if (String.ISBLANK(errorLog.Object_Type__c)) 
                {
                    errorLookup = getErrorDescription(errorLog.Error_Code__c);
                } 
                else 
                {
                    errorLookup = getErrorDescription(DML_EXCEPTION);
                }
                //Show the Error Message on the Page 
                addMessageToPage(mapSeverityTypes.get(errorLookup.Severity__c),  errorLookup.User_Friendly_Message__c + ' : ' +errorLog.Name, bShowErrorOnPage);
            }
        }
    }

    /* Method Name   :    saveLog 
     * Description   :    Save the Error logs
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void saveLog() 
    {
        saveLog(listErrorLogs, false);
    }

    /* Method Name   :    addMessageToPage
     * Description   :    Show the Message on the Page
     * Return Type   :    void
     * Parameters    :    NA
     */
    public static void addMessageToPage(ApexPages.Severity severity, String sMessage, boolean bShowErrorMessage) 
    {
        Set<String> setErrorMessagesOnPage = new Set<String>();
        
        if (ApexPages.CurrentPage() != null && bShowErrorMessage) 
        {
            for(ApexPages.Message msg:ApexPages.getMessages())
            {
                setErrorMessagesOnPage.add(msg.getDetail());
            }
             if(!setErrorMessagesOnPage.contains(sMessage.trim())) { ApexPages.addMessage(new ApexPages.Message(severity, sMessage)); }
        }
    }

    /* 
    * Method name : logError
    * Description : This method will log the error details to Error_Log__c object from LWC component
    * Parameter   : String variables
    * 
    * string sMessage: error message 
    * string sClass: Class Name 
    * string sMethod: Method Name 
    * string sExceptionType: Ecpetion type
    * string sErrorType: Error Type
    */
    @AuraEnabled
    public static string logError(string sMessage, string sClass, string sMethod, string sExceptionType, string sErrorType)
    {
        Error_Log__c oError = new Error_Log__c();
        string sErroLogNumber;
        oError.Error_Code__c = sExceptionType;
        oError.Error_Message__c = sMessage;        
        oError.class_name__c = sClass;
        oError.Method_Name__c = sMethod;
        oError.Type__c = sErrorType;
        Database.SaveResult oDMLSaveResults = Database.insert(oError, false);
        
        if(oDMLSaveResults.isSuccess())
        {
            oError = [Select Id, Name from Error_Log__c where id=:oDMLSaveResults.getId()];
            sErroLogNumber = oError.Name;
        }
        return sErroLogNumber;
    }
}