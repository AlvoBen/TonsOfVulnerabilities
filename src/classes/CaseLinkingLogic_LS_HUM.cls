/****************************************************************************************************************************
Apex Class Name  : CaseLinkingLogic_LS_HUM
Version          : 1.0
Created Date     : 12/03/2021
Function         : This is service layer class used to link two are more cases.
Test Class       : CaseLinkingLogic_LT_HUM
****************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                               12/03/2021                US- 2081786 Case Management - Case Linking
************************************************************************************************************************************/

public with sharing class CaseLinkingLogic_LS_HUM
{
    private Static Map<String ,List<String>> mapCombinations = new Map<String ,List<String>>();
    Private Static set<String> existingCaseLinks = new Set<String>();
    Private Static List<Case_Junction__c> lstNewLinkRecords = new List<Case_Junction__c>();
    
    /*
     * Method Name : LinkCases
     * Description : This method is used to link given cases.
     * Return type : Boolean
     * Paramater   : String 
     */
    public static Boolean linkCases(List<String> sSelectedCaseIds)
    {
        Boolean isLinkingSuccess = false;
        try
        {
            Map<String,Boolean> mapCaseDetails = new Map<String,Boolean>();
            List<String> lstNewCaseIds = new List<String>();
            
            //List<String> lstCaseIds = sSelectedCaseIds.split(',');
            mapCaseDetails = prepareCaseSecurityMap(sSelectedCaseIds ,mapCaseDetails );
            
            existingCaseLinks =  retrieveCaseJunctionRecords(mapCaseDetails.keySet());
            lstNewCaseIds.AddAll(mapCaseDetails.keySet());
            mapCombinations = prepareNewCaseLinkCombinations(lstNewCaseIds);
            createCaseJunctionRecords(mapCaseDetails );
            if(lstNewLinkRecords != Null && !lstNewLinkRecords.isEmpty())
            {
                insert lstNewLinkRecords;   
                isLinkingSuccess = true;
            }
            else
            {
                isLinkingSuccess = false;
            }
        }
        catch(Exception ex)
        {
             isLinkingSuccess = false;
             HUMExceptionHelper.logErrors(ex,'CaseLinkingLogic_LS_HUM','LinkCases');
        }
        return isLinkingSuccess ; 
    }
    
    /*
     * Method Name : createCaseJunctionRecords
     * Description : This method is used to create case junction records.
     * Return type : NA
     * Paramater   : Map<String,Boolean>
     */
    @testVisible private static void createCaseJunctionRecords(Map<String,Boolean> mapCaseDetails )
    {
        Case_Junction__c objCaseJun = Null;
        Id objWQId = getWorkQueueId();
        
        For(String sKey : mapCombinations.keySet())
        {
            List<String> lstCombinationCases = mapCombinations.get(sKey);
            for(String objId : lstCombinationCases)
            {
                If(!existingCaseLinks.contains(sKey+'_'+objId) && !existingCaseLinks.contains(objId +'_'+ sKey))
                {
                    objCaseJun = new Case_Junction__c();
                    if(mapCaseDetails.get(sKey))
                    {
                        if(Schema.SObjectType.Case_Junction__c.fields.Case_Master__c.isCreateable())	objCaseJun.Case_Master__c = sKey;
                        if(Schema.SObjectType.Case_Junction__c.fields.Case__c.isCreateable())	objCaseJun.Case__c = objId;
                    }
                    else
                    {
                        if(Schema.SObjectType.Case_Junction__c.fields.Case__c.isCreateable())	objCaseJun.Case__c = sKey;
                        if(Schema.SObjectType.Case_Junction__c.fields.Case_Master__c.isCreateable())	objCaseJun.Case_Master__c = objId;
                    }
                    if(Schema.SObjectType.Case_Junction__c.fields.Work_Queue__c.isCreateable())	objCaseJun.Work_Queue__c = objWQId;
                    lstNewLinkRecords.Add(objCaseJun);
                }
            }
        }
    }
    
    /*
     * Method Name : getWorkQueueId
     * Description : This method is used to retrieve logged in users current queue id.
     * Return type : Id 
     * Paramater   : NA
     */
    @testVisible private static Id getWorkQueueId()
    {
        Id objId = Null;
        User objUser = [Select Id, Current_Queue__c from User where Id =: UserInfo.getUserId()];
        Work_Queue_Setup__C objWQS = [Select Id from Work_Queue_Setup__C where Name =: objUser.Current_Queue__c AND IsActive__c = TRUE];
        if(objWQS != Null) objId = objWQS.Id;
        return objId;
    }
    
    /*
     * Method Name : prepareNewCaseLinkCombinations
     * Description : This method is used to prepare case link combinations.
     * Return type : Map<String ,List<String>>
     * Paramater   : List<String>
     */
    @testVisible private static Map<String ,List<String>> prepareNewCaseLinkCombinations(List<String> lstNewCaseIds)
    {
        List<String> lstIds = Null;
        Map<String ,List<String>> mapTempCombinations = new Map<String ,List<String>>();
        For(integer i = 0; i< lstNewCaseIds.size(); i++)
        {
            lstIds = new List<String>();
            For(integer j= i+1;j<= lstNewCaseIds.size()-1;j++)
            {
                lstIds.Add(lstNewCaseIds[j]);
            } 
            mapTempCombinations.put(lstNewCaseIds[i],lstIds); 
        }
        return mapTempCombinations; 
    }
    
    /*
     * Method Name : retrieveCaseJunctionRecords
     * Description : This method is used to retrieve case junction records.
     * Return type : Set<String>
     * Paramater   : Set<String>
     */
    @testVisible private static Set<String> retrieveCaseJunctionRecords(Set<String> lstCases)
    {
        Set<String> setTempValues = new Set<String>();
        
        List<Case_Junction__c> lstLinkRecords = [Select Id,Name,Case_Master__c,Case__c from 
                                    Case_Junction__c where IsLinked__c = true AND (Case_Master__c in : lstCases  OR Case__c in : lstCases)];
                                    
        If(lstLinkRecords != null && !lstLinkRecords.isEmpty())
        {
            For(Case_Junction__c obj : lstLinkRecords)
            {
                setTempValues.Add(obj.Case_Master__c +'_'+ obj.Case__c);
                setTempValues.Add(obj.Case__c +'_'+ obj.Case_Master__c);       
            }
        }
        return setTempValues;
    }
    
    /*
     * Method Name : prepareCaseSecurityMap
     * Description : This method is used to create a map with case id as key and security as value.
     * Return type : Map<String,Boolean>
     * Paramater   : List<String>,Map<String,Boolean>
     */
    @testVisible private static Map<String,Boolean> prepareCaseSecurityMap(List<String> lstCases ,Map<String,Boolean> mapCaseDetails)
    {
       for(String sObj : lstCases )
       {
           mapCaseDetails.put(sObj.substring(0,sObj.indexOf('_')),Boolean.valueOf(sObj.substring(sObj.indexOf('_')+1,sObj.length())));
       }
       return mapCaseDetails;
    }
}