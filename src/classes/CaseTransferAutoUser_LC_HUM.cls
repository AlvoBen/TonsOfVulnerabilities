/*******************************************************************************************************************************
Apex Class Name : CaseTransferAutoUser_LC_HUM 
Version         : 1.0
Created On      : 02/25/2022
Function        : This class returns the list of users corresponsing to the selected Queue.
Test Class      : CaseTransferAutoUser_LT_HUM                
                 
Modification Log: 
* Developer Name         Code Review            Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
Ankima                                          02/25/2022                 Original version
*******************************************************************************************************************************/
global with sharing class CaseTransferAutoUser_LC_HUM
{
    public static Set<Id> setUserId = new Set<Id>();
    public static Set<Id> setFinalUserId = new Set<Id>();

    
    /*
     * Method Name : searchObject
     * Description : It takes parameter from component and do search on the basis of parameters passed
     * Return type : List of sObject
     * Paramater   : object name, queue name, Query, size of result, wild card option
     */
     @AuraEnabled(cacheable=true)
    global static list<sObject> searchObject(string sObjectName, string sQueueName, string sQuery)
    {
        //sQueueName='Louisville_Medicare_Nonstandard_Claims';
        String sSoqlQuery = 'Select Id, Name, Email From ' + sObjectName + ' Where Name like \'';
        
        Map<String, Schema.SObjectType> mapGroupDesc = Schema.getGlobalDescribe();
        Schema.SObjectType sot = mapGroupDesc.get(sObjectName);
        if(!String.isBlank(sQueueName))
        {
            Id grpId = [Select Id From Group Where type=: System.Label.HUM_QUEUE and DeveloperName =: sQueueName].Id;
            Set<String> setIds = new Set<String>();
            setIds.Add(grpId);
            setFinalUserId = getAllUsers(setIds);
        }
        else
        {
            return null;
        }
        
        sSoqlQuery += '%';
        
        sSoqlQuery += String.escapeSingleQuotes(sQuery) + '%\'' + 'AND Id IN : setFinalUserId ORDER BY Name LIMIT 50000';
      //  sSoqlQuery += String.escapeSingleQuotes(sQuery) + '%\'' + 'ORDER BY Name LIMIT 50000';
        List<sObject> lstResults = new List<sObject>();
        try
        {
            lstResults = Database.query(sSoqlQuery);
        }
        catch (QueryException e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTransferAutoUser_LC_HUM', 'searchObject');
            return null;
        }
        return lstResults;
    }
    
    /*
    * Method Name  : getAllUsers
    * Description  : It takes parameter as GroupId and returns all Users associated with it
    * Return type  : Set of Id
    * Paramaters   : Group Id
    */
    @testVisible private static Set <Id> getAllUsers(Set<String> setGrpId)
    {
         if(setGrpId != NULL)
         {
             List<GroupMember> lstGroup = [Select UserorGroupId from GroupMember where Group.Id IN: setGrpId];
             String sGrpOrUserId;
             Set<String> setInnerGroupIds = new Set<String>();
             for(GroupMember oGrp : lstGroup)
             {
                sGrpOrUserId = oGrp.UserorGroupId;
                
                if(sGrpOrUserId.startsWith(System.Label.HUM_SFDC_USER_ID))
                {
                    setUserId.add(oGrp.UserorGroupId);
                }
                else if(sGrpOrUserId.startsWith(System.Label.HUM_SFDC_GROUP_ID))
                {
                    setInnerGroupIds.Add(sGrpOrUserId);
                }
            }
            if(setInnerGroupIds != Null && !setInnerGroupIds.isEmpty()) getAllUsers(setInnerGroupIds);
        }
        return setUserId;
    }
}