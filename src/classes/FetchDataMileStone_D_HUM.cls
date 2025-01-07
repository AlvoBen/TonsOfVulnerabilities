/*****************************************************************************************************
 Apex Class Name  : FetchDataMileStone_D_HUM
 Version   : 1.0 
 Created Date  : 12/30/2016
 Function  : Data Layer Class for MileStone New/Edit Page
 Test Class  :  FetchDataMileStone_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------                 
* Prasanthi Kandula            55297           12/30/2016              Original Version - REQ - 290027 MileStone detail page
* Manoj Chaurasia              202594          03/29/2018              REQ - 354470 Ability to display TAS combination in CRM
* Samantha Tennyson            		       08/25/2020              User Story 1318943: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (RF)
* Vamshi Krishna Pemberthi 	   					10/05/2021			    SOQL Injection High checkmarx fix
********************************************************************************************************************************/

public with sharing class FetchDataMileStone_D_HUM
{
    /*
    * Method Name: queryMileStoneData
    * Description: This method will query MileStone record data
    * Parameter: String sMileStoneID - MileStone id
    * Return Type: MET_Milestone__c
    */
    public MET_Milestone__c queryMileStoneData(String sMileStoneID)
    {
        MET_Milestone__c oMetMileStone = [select id,Name,Ownerid,Owner.Name, met_entries_assoc__c, Met_Entries_Assoc_Order__c, Action__c,Action__r.id,Task__r.id,Task__r.Name,  Action__r.Name,Source_Name__c,Source_Name__r.id, Source_Name__r.Name, Case_Type__c, Case_SubType__c, Is_Active__c, Task__c, Is_Milestone__c FROM MET_Milestone__c WHERE id=:sMileStoneID]; 
        return oMetMileStone;
    }
    
    /*
    * Method Name: queryTaskData
    * Description: This method queries MET Task data. Dynamic query formed based on the parameters passed.
    * Parameter: String sCaseSubType - Case SubType value from MET_Task__c
                 String SearchString - String typed in Search text box holds the name of Task
                 String sSortExpression - Sort Expression
                 String sSortDirection - Sort Direction  
    * Return Type: List<sObject>
    */
    public List<sObject> queryTaskData(String sCaseSubType, String searchString,String sSortExpression, String sSortDirection)
    {
        string sSoql =  'select id,Name,Case_Subtype__c,Is_Active__c from MET_Task__c where Is_Active__c=true';
        string sSearchString= '%' + searchString + '%';
        if(String.isNotBlank(sCaseSubType))
        {
            sSoql = sSoql + ' and Case_Subtype__c=:sCaseSubType';
        }
        if(String.isNotBlank(sSearchString))
        {
            sSoql = sSoql +  ' and name LIKE :sSearchString ';
        }
        if(String.isNotBlank(sSortExpression) && String.isNotBlank(sSortDirection)) 
        {
            sSoql += ' ORDER BY ' + sSortExpression+ ' ' + sSortDirection+ ' NULLS LAST';
        }
        return database.query(sSoql);
    }
    
    /*
    * Method Name: queryActionData
    * Description: This method queries MET Action data. 
    * Parameter: Set<Id> setActionId - Set of MET Action Ids
                 String sSortExpression - Sort Expression
                 String sSortDirection - Sort Direction  
    * Return Type: List<sObject>
    */
    public List<sObject> queryActionData(Set<Id> setActionId,String sSortExpression, String sSortDirection)
    {
        string sSoql =  'select id,name from MET_Action__c where Id in :setActionId';
        if(String.isNotBlank(sSortExpression) && String.isNotBlank(sSortDirection)) 
        {
            sSoql += ' ORDER BY ' + sSortExpression+ ' ' + sSortDirection+ ' NULLS LAST';
        }
        return database.query(sSoql); 
    }
    
    /*
    * Method Name: queryActionDataWithoutTask
    * Description: This method queries MET Action data where MET Task Name is Blank. 
    * Parameter: String searchString - Action Name
                 String sSortExpression - Sort Expression
                 String sSortDirection - Sort Direction  
    * Return Type: List<sObject>
    */
    public List<sObject> queryActionDataWithoutTask(String searchString, String sSortExpression, String sSortDirection)
    {
        string sSoql =  'select id,name from MET_Action__c where Is_Active__c = true';
        string sSearchString= '%' + searchString + '%';
        if(String.isNotBlank(sSearchString))
        {
           sSoql = sSoql +  ' and Name LIKE :sSearchString ';
        }
        if(String.isNotBlank(sSortExpression) && String.isNotBlank(sSortDirection)) 
        {
            sSoql += ' ORDER BY ' + sSortExpression+ ' ' + sSortDirection+ ' NULLS LAST';
        }
        return database.query(sSoql);
    }
   
    /*
    * Method Name: querySourceData
    * Description: This method queries MET Source data. 
    * Parameter: Set<Id> setSourceId - Set of MET Source Ids
                 String sSortExpression - Sort Expression
                 String sSortDirection - Sort Direction  
    * Return Type: List<sObject>
    */
    public List<sObject> querySourceData(Set<Id> setSourceId,String sSortExpression, String sSortDirection)
    {
        string sSoql =  'select id,name from MET_Source__c where Id in :setSourceId';
        if(String.isNotBlank(sSortExpression) && String.isNotBlank(sSortDirection)) 
        {
            sSoql += ' ORDER BY ' + sSortExpression+ ' ' + sSortDirection+ ' NULLS LAST';
        }
        return database.query(sSoql);
    }
    
    /*
    * Method Name: querySourceDataWithoutAction
    * Description: This method queries MET Source records 
    * Parameter: String searchString - Source Name
                 String sSortExpression - Sort Expression
                 String sSortDirection - Sort Direction  
    * Return Type: List<sObject>
    */
    public List<sObject> querySourceDataWithoutAction(String searchString, String sSortExpression, String sSortDirection)
    {
        string sSoql =  'select id,name from MET_Source__c where Is_Active__c = true';
        string sSearchString= '%' + searchString + '%';
        if(String.isNotBlank(sSearchString))
        {
           sSoql = sSoql +  ' and Name LIKE :sSearchString ';
        }
        if(String.isNotBlank(sSortExpression) && String.isNotBlank(sSortDirection)) 
        {
            sSoql += ' ORDER BY ' + sSortExpression+ ' ' + sSortDirection+ ' NULLS LAST';
        }
        return database.query(sSoql);
    }   
}