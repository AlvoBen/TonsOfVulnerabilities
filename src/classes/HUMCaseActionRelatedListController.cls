/******************************************************************************************************************************
Apex Class Name  : HUMCaseActionRelatedListController 
Version          : 1.0 
Created Date     : JUL 24 2014
Function         : Controller to handle display Related section on Case detail page.                   
Test Class       : HUMCaseActionRelatedListControllerTest
Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Vivek Batham        17353           07/24/2014           Original version.
 *    Vivek Batham        17840           09/10/2014           Made changes according to Humana standards
 *********************************************************************************************************************************/
public with sharing class HUMCaseActionRelatedListController 
{
    private Case casObj = new Case();
    
    /*
     * Method Name : HUMCaseActionRelatedListController
     * Description : constructor for the class. Initialized variables and fetched query results.
     * Return type : Void
     * Paramater   : ApexPages.StandardController
     */
    public HUMCaseActionRelatedListController(ApexPages.StandardController controller) 
    {
        listProcess = new List<Case_Action_Association__c>();
        if(controller!=null) 
        { 
            casObj = (Case)controller.getRecord();
            if(casObj.id!=null)
            {
                listProcess = [Select LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, 
                               CreatedById, Case__c, Case_Action__r.Name, Case_Action__c, Action__c 
                               From Case_Action_Association__c WHERE Case__c =: casObj.Id];
            }
        } 
    }
    
    public List<Case_Action_Association__c> listProcess {get; set;}
}