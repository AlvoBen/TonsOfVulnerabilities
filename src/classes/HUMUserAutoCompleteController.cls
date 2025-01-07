/*******************************************************************************************************************************
Apex Class Name : HUMUserAutoCompleteController 
Version         : 1.0
Created On      : MAY 22 2014
Function        : Controller to HUMUserAutoComplete Component. This controller is contains a remote action method which is called from VF component
                   to query over User records when User is typing values in text box field on page.
Test Class      : HUMUserAutoCompleteControllerTest                
                 
Modification Log: 
* Developer Name         Code Review            Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sagar Tapadia          17093                  22/05/2014                 Original Version
* Manish Kumar           17093                  23/08/2014                 Defect fixed : 150331
* SuryaKumari Medicherla 21223                  27/02/2015                 Replaced Name with DeveloperName in SOQL while querying Group Object of Type Queue to fix production issue 146
* Santhi Mandava                                03/12/2021                 Fixed user not found issue.
*******************************************************************************************************************************/
global with sharing class HUMUserAutoCompleteController
{
    @testVisible private static Set<Id> setUserId = new Set<Id>();
    @testVisible private static Set<Id> setFinalUserId = new Set<Id>();

    
    /*
     * Method Name : searchObject
     * Description : It takes parameter from component and do search on the basis of parameters passed
     * Return type : List of sObject
     * Paramater   : object name, queue name, Query, size of result, wild card option
     */
    @RemoteAction
    global static list<sObject> searchObject(string sObjectName, string sQueueName, string sQuery, String sNumberOfResults, String sWildCardOption)
    {
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
        
        if (sWildCardOption == 'true')  sSoqlQuery += '%';
        
        sSoqlQuery += String.escapeSingleQuotes(sQuery) + '%\'' + 'AND Id IN : setFinalUserId ORDER BY Name';
        
        List<sObject> lstResults = new List<sObject>();
        try
        {
            lstResults = Database.query(sSoqlQuery);
        }
        catch (QueryException e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUserAutoCompleteController', 'searchObject');
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