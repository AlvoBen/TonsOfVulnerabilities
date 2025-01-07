/******************************************************************************************************************************
Apex Class Name  : MedicaidPARProviderNotAccepting_C_HUM
Version          : 1.0 
Created Date     : JAN 21 2021
Function         : Controller to handle display "Medicaid PAR Provider Not Accepting" form.                   
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Y Sunil Kumar Reddy                   01/21/2021             Original Version - User Story 1763850: PR00091574 - MF 1 - Quality - "Medicaid PAR Provider Not Accepting" template and case validations (CRM)
* Y Sunil Kumar Reddy                   04/06/2021             User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
 *********************************************************************************************************************************/
public with sharing class MedicaidPARProviderNotAccepting_C_HUM extends HUMTemplateBaseHelper {
    list<Template_Submission__c> oTemplateSubmssion = new list<Template_Submission__c>();
    String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

    public MedicaidPARProviderNotAccepting_C_HUM(ApexPages.StandardController controller) 
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
    public void getSubmissionId() {
        if(String.IsNotBlank(sSelectedCaseId)){
            oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase) order by createddate desc limit 1];              
            if(!oTemplateSubmssion.isempty()){
                SubmissionId = oTemplateSubmssion[0].Id;
                bSubmissionPres = true;
            }
        }  
    }
    
   /*
    * Method Name   :    getFinishPage
    * Description   :    sets the finish page and pass parameter 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference getFinishPage()
    {
         PageReference prRef = New PageReference('/apex/HUMCloseFlow');
         prRef.getParameters().put('MedicaidPARCaseId',sSelectedCaseId);
         prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,System.Label.MedicaidPARProviderNotAccepting_HUM);
         prRef.setRedirect(true);
         return prRef;
         
   }
}