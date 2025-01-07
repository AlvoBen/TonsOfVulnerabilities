/*****************************************************************************************************************************************************************************
Apex Class Name : CRM_GenericHelper_H_HUM
Version         : 1.0
Created On      : 09/17/2021
Test Class      : CRM_GenericHelper_T_HUM
Modification Log: 

 * Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Nikhil Malhotra                                                  09/17/2021                REQ - 2668033 Generic method for FLS security check.
*Arpit Jain                                                       05/01/2022                Generic method returning FLS security check error message.
**************************************************************************************************************************************************/
public with sharing class CRM_GenericHelper_H_HUM {
    
    public class accessibilityOutput{
        
        public Boolean isObjectAccessible = true;
        public Boolean areFieldsAccessible = true;
        public Boolean exceptionGenerated = false;
        public Exception exceptionToLog;
        public Set<String> fieldsNotAccessible = new Set<String>();
        public String objectType = '';
    }
    
    /*
    * Method Name   : securityCheckFLS
    * Description   : method to check FLS and object accessibility.     
    * Parameters    : System.AccessType accessCheckType, List<SObject> sourceRecords  
    * retunr type   : accessibilityOutput 
    */
    public static accessibilityOutput securityCheckFLS(System.AccessType accessCheckType, List<SObject> sourceRecords)        
    {
        accessibilityOutput accessbilityObj  = new accessibilityOutput();
        try
        {
            accessbilityObj.objectType = String.valueOf(sourceRecords.getSObjectType());
            SObjectAccessDecision securityDecision = Security.stripInaccessible(accessCheckType, sourceRecords);
            if(securityDecision.getRemovedFields().get(accessbilityObj.objectType) != null)
            {
                accessbilityObj.fieldsNotAccessible = securityDecision.getRemovedFields().get(accessbilityObj.objectType); 
                accessbilityObj.areFieldsAccessible = false;
            }
        }
        catch(NoAccessException e)
        {
             accessbilityObj.isObjectAccessible = false; 
        }
        catch(Exception e)
        {
            accessbilityObj.exceptionGenerated = true; 
            accessbilityObj.exceptionToLog = e;
        }
        return accessbilityObj;
    }
    
    /*
    * Method Name   : flsCheck
    * Description   : method to check FLS and object accessibility and return error message.     
    * Parameters    : System.AccessType accessCheckType, List<SObject> sourceRecords  
    * retunr type   : String(Error message) 
    */
    public static String flsCheck(AccessType action,List<Sobject> objectList) 
    {
        String FLSError = '';
        accessibilityOutput accessbilityObj = securityCheckFLS(action,objectList);  
        String actionName = String.valueOf(action) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER_ACTIONTYPE_CREATABLE ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DML_CREATE_ACCESS : String.valueOf(action) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER_ACTIONTYPE_UPDATABLE ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DML_UPDATE_ACCESS : String.valueOf(action) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER_ACTIONTYPE_READABLE ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DML_READ_ACCESS : String.valueOf(action) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER_ACTIONTYPE_UPSERTABLE ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DML_UPSERT_ACCESS:'';


        if(!accessbilityObj.isObjectAccessible){
            FLSError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER+userInfo.getName() + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_INSUFFICIENT_ACCESS + actionName+' '+
            + accessbilityObj.objectType;
        }
        else if(!accessbilityObj.areFieldsAccessible){
            FLSError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_USER+userInfo.getName() + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_INSUFFICIENT_FIELD_ACCESS
            + accessbilityObj.fieldsNotAccessible;
        }            
        else if(accessbilityObj.exceptionGenerated)
        {
            FLSError = accessbilityObj.exceptionToLog.getMessage();
        }
        return FLSError;
    }
}