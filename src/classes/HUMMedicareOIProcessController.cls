/******************************************************************************************************************************
Apex Class Name  : HUMMedicareOIProcessController 
Version          : 1.0 
Created Date     : May 19 2015
Function         : Controller to handle display medicare OI form.                   
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
   * Santhi Mandava     24116               05/19/2015           Original version.
   * Tanushree Hazari   24116               06/19/2015           Modified for passing templatesubmissionId to flow
   * Jasmeen Shangari                       04/09/2020           942030 - Added 'Send Print Item: Member' process in filter criteria   
 *********************************************************************************************************************************/

public with sharing class HUMMedicareOIProcessController extends HUMTemplateBaseHelper 
{
    Template_Submission__c oTemplateSubmssion = new Template_Submission__c();
    String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);
    
    /*
    * Method Name   :    HUMMedicareOIProcessController
    * Description   :    Constructor for the class 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMMedicareOIProcessController(ApexPages.StandardController controller) 
    {
        bSubmissionPres  = false; 
        getSubmissionId();
    }
    
    public Id SubmissionId {get;set;}
    public Boolean bSubmissionPres {get;set;}
    
    /*
    * Method Name   :    getSubmissionId
    * Description   :    gets the submission Id of the Case 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public void getSubmissionId()
    {
        try
        {
            bSubmissionPres = true;
            oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId AND Object_Owner_Type__c = : sCase)];
            SubmissionId = oTemplateSubmssion.Id;
        }
        catch(Exception ex) 
        {
            SubmissionId = null;
            bSubmissionPres  = false; 
        } 
    }
    
}