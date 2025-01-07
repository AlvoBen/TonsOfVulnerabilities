/*****************************************************************************************************************************************************************************************************************************************************
Apex Class Name  : HUMNewInteractionController
Version          : 1.0
Created Date     : Jan 7 2015
Function         : Class to populate interaction details from case
Test Class       : HUMNewInteractionControllerTest

******************************************************************************************************************************************************************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Pradeep Raju                  20311              01/07/2015                    Original Version
* Pradeep Raju                  20311              01/13/2015                    Updated the class to include save logic
* Praveen Kumar Parimi          94071              05/16/2017                    REQ-313373/Fix for CA ticket 6746409
*****************************************************************************************************************************************************************************************************************************************************/

public with sharing class HUMNewInteractionController
{
    
    private case caseRecord = new case();
    private String sId ='';
    
    /*
    * Method name  : HUMCreateInteractionFromCase
    * Description  : Constructor to get the case id from the URL parameters
    * Return Type  : NA
    * Parameter    : NA
    */
    public HUMNewInteractionController()
    {
        interactionRec =  new Interaction__c();
        sId =  ApexPages.Currentpage().getParameters().get('id');
        bAccount = false;
    }
    
    public Interaction__c interactionRec{get;set;}
    public Boolean bAccount {get;set;}  
    
    /*
    * Method name  : redirectToInteraction
    * Description  : Method to populate interaction details from case on page load
    * Return Type  : PageReference
    * Parameter    : NA
    */
    public pageReference redirectToInteraction()
    {
        try
        {           
            if(string.isnotBlank(sId) && sId.startswith('001'))
            {
                interactionRec.Interacting_With__c  = sId;
                bAccount = true;
            }
           
            else
            {
                caseRecord = [select id, Origin, Account.Id, Interacting_With__c, Interacting_with_type__c, Account.RecordType.Name, Interacting_About__c, Interacting_About_Type__c from case where id=:sId];
                if(caseRecord!=NULL)
                {
                    interactionRec.Interaction_Origin__c = caseRecord.Origin;
                    interactionRec.Interacting_With__c = caseRecord.Interacting_With__c;
                    interactionRec.Interacting_With_type__c = caseRecord.Interacting_With_Type__c;
                }
            }
           
            return null;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMNewInteractionController', 'redirectToInteraction');
            return null;
        }
    }
    
    /*
    * Method name  : save
    * Description  : Method to open interaction detail page along with related records once use clicks on save
    * Return Type  : PageReference
    * Parameter    : NA
    */
    public pageReference save()
    {
        try
        {
            List<Interaction__c> lstInteraction = new List<Interaction__c>{interactionRec};
            List<Database.SaveResult> savResults = Database.insert(lstInteraction,false);
            HUMExceptionHelper.processSaveResults(savResults ,lstInteraction ,'HUMNewInteractionController','save','Interaction__c');
            
            if(savResults[0].isSuccess())
            {
                if(caseRecord.Account.Id!=null)
                {
                    Interaction_Member__c interactionMember = new Interaction_Member__c();
                    interactionMember.Interacting_About__c = caseRecord.Account.Id;
                    interactionMember.Interacting_About_Type__c = caseRecord.Account.RecordType.Name;
                    interactionMember.Interaction__c = interactionRec.id;
                    
                    List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>{interactionMember};
                    List<Database.SaveResult> saveIntMem = Database.insert(lstInteractionMember,false);
                    HUMExceptionHelper.processSaveResults(saveIntMem ,lstInteractionMember ,'HUMNewInteractionController','save','Interaction_Member__c');
                }
                
                Case_Interaction__c caseInteraction = new  Case_Interaction__c();
                caseInteraction.Interaction__c = interactionRec.Id;
                caseInteraction.Case__c = caseRecord.Id;
                
                List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>{caseInteraction};
                List<Database.SaveResult> saveCaseInt = Database.insert(lstCaseInteraction,false);
                HUMExceptionHelper.processSaveResults(saveCaseInt ,lstCaseInteraction ,'HUMNewInteractionController','save','Case_Interaction__c');
                
                pageReference pgRef = new pageReference(+'/'+interactionRec.id);
                pgRef.setRedirect(true);
                return pgRef;
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMNewInteractionController','save');
            return null;
        }
    }
    
    /*
    * Method name  : Cancel
    * Description  : Method to redirect to case detail page on click of cancel
    * Return Type  : pageReference
    * Parameter    : NA
    */
    public pageReference cancel()
    {
        try
        {
            pageReference pgRef = new pageReference(+'/'+caseRecord.id);
            pgRef.setRedirect(true);
            return pgRef;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMNewInteractionController','cancel');
            return null;
        }
    }
}